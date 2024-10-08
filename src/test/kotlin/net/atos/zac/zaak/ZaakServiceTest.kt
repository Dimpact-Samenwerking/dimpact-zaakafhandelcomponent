package net.atos.zac.zaak

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.checkUnnecessaryStub
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.atos.client.zgw.shared.model.Archiefnominatie
import net.atos.client.zgw.zrc.ZrcClientService
import net.atos.client.zgw.zrc.model.BetrokkeneType
import net.atos.client.zgw.zrc.model.createZaak
import net.atos.client.zgw.ztc.ZtcClientService
import net.atos.client.zgw.ztc.model.createRolType
import net.atos.client.zgw.ztc.model.generated.OmschrijvingGeneriekEnum
import net.atos.zac.event.EventingService
import net.atos.zac.event.Opcode
import net.atos.zac.identity.model.createGroup
import net.atos.zac.identity.model.createUser
import net.atos.zac.websocket.event.ScreenEvent
import net.atos.zac.websocket.event.ScreenEventType

class ZaakServiceTest : BehaviorSpec({
    val eventingService = mockk<EventingService>()
    val zrcClientService = mockk<ZrcClientService>()
    val ztcClientService = mockk<ZtcClientService>()
    val zaakService = ZaakService(
        eventingService = eventingService,
        zrcClientService = zrcClientService,
        ztcClientService = ztcClientService
    )
    val explanation = "dummyExplanation"
    val screenEventResourceId = "dummyResourceId"
    val zaken = listOf(
        createZaak(),
        createZaak()
    )
    val group = createGroup()
    val user = createUser()
    val rolTypeBehandelaar = createRolType(
        omschrijvingGeneriek = OmschrijvingGeneriekEnum.BEHANDELAAR
    )

    beforeEach {
        checkUnnecessaryStub()
    }

    Given("A list of open zaken and a group and a user") {
        val screenEventSlot = slot<ScreenEvent>()
        zaken.map {
            every { zrcClientService.readZaak(it.uuid) } returns it
            every {
                ztcClientService.readRoltype(
                    it.zaaktype,
                    OmschrijvingGeneriekEnum.BEHANDELAAR
                )
            } returns rolTypeBehandelaar
            every { zrcClientService.updateRol(it, any(), explanation) } just Runs
            every { eventingService.send(capture(screenEventSlot)) } just Runs
        }
        When(
            """the assign zaken function is called with a group, a user
                and a screen event resource id"""
        ) {
            zaakService.assignZaken(
                zaakUUIDs = zaken.map { it.uuid },
                explanation = explanation,
                group = group,
                user = user,
                screenEventResourceId = screenEventResourceId
            )

            Then(
                """for both zaken the group and user roles 
                    and the search index should be updated and
                    a screen event of type 'zaken verdelen' should sent"""
            ) {
                zaken.map {
                    verify(exactly = 2) {
                        zrcClientService.updateRol(it, any(), explanation)
                    }
                }
                with(screenEventSlot.captured) {
                    opcode shouldBe Opcode.UPDATED
                    objectType shouldBe ScreenEventType.ZAKEN_VERDELEN
                    objectId.resource shouldBe screenEventResourceId
                }
            }
        }
    }
    Given("One open and one closed zaak and a group and a user") {
        val openZaak = createZaak()
        val closedZaak = createZaak(
            archiefnominatie = Archiefnominatie.VERNIETIGEN
        )
        val zakenList = listOf(openZaak, closedZaak)
        val screenEventSlot = slot<ScreenEvent>()
        zakenList.map {
            every { zrcClientService.readZaak(it.uuid) } returns it
        }
        every {
            ztcClientService.readRoltype(
                openZaak.zaaktype,
                OmschrijvingGeneriekEnum.BEHANDELAAR
            )
        } returns rolTypeBehandelaar
        every { zrcClientService.updateRol(openZaak, any(), explanation) } just Runs
        every { eventingService.send(capture(screenEventSlot)) } just Runs
        When(
            """the assign zaken function is called with a group, a user
                and a screen event resource id"""
        ) {
            zaakService.assignZaken(
                zaakUUIDs = zakenList.map { it.uuid },
                explanation = explanation,
                group = group,
                user = user,
                screenEventResourceId = screenEventResourceId
            )

            Then(
                """
                    only for the open zaak the group and user roles 
                    and the search index should be updated and
                    a screen event of type 'zaken verdelen' should sent
                    and the closed zaak should be skipped
                    """
            ) {
                // only the open zaak should have group and user roles assigned to it
                verify(exactly = 2) {
                    zrcClientService.updateRol(openZaak, any(), explanation)
                }
                with(screenEventSlot.captured) {
                    opcode shouldBe Opcode.UPDATED
                    objectType shouldBe ScreenEventType.ZAKEN_VERDELEN
                    objectId.resource shouldBe screenEventResourceId
                }
            }
        }
    }
    Given("A list of zaken and a screen event resource id") {
        val screenEventSlot = slot<ScreenEvent>()
        zaken.map {
            every { zrcClientService.readZaak(it.uuid) } returns it
            every { zrcClientService.deleteRol(it, any(), explanation) } just Runs
        }
        every { eventingService.send(capture(screenEventSlot)) } just Runs
        When(
            """the release zaken function is called with
                 a screen event resource id"""
        ) {
            zaakService.releaseZaken(
                zaakUUIDs = zaken.map { it.uuid },
                explanation = explanation,
                screenEventResourceId = screenEventResourceId
            )
            Then(
                """both zaken should no longer have a user assigned
                     but the group should still be assigned
                    and the search index should be updated and
                    a screen event of type 'zaken vrijgeven' should sent"""
            ) {
                zaken.map {
                    verify(exactly = 1) {
                        zrcClientService.deleteRol(it, BetrokkeneType.MEDEWERKER, explanation)
                    }
                }
                with(screenEventSlot.captured) {
                    opcode shouldBe Opcode.UPDATED
                    objectType shouldBe ScreenEventType.ZAKEN_VRIJGEVEN
                    objectId.resource shouldBe screenEventResourceId
                }
            }
        }
    }
    Given("One open and one closed zaak and a screen event resource id") {
        val openZaak = createZaak()
        val closedZaak = createZaak(
            archiefnominatie = Archiefnominatie.VERNIETIGEN
        )
        val zakenList = listOf(openZaak, closedZaak)
        val screenEventSlot = slot<ScreenEvent>()
        zakenList.map {
            every { zrcClientService.readZaak(it.uuid) } returns it
        }
        every { zrcClientService.deleteRol(openZaak, any(), explanation) } just Runs
        every { eventingService.send(capture(screenEventSlot)) } just Runs
        When(
            """the release zaken function is called with
                 a screen event resource id"""
        ) {
            zaakService.releaseZaken(
                zaakUUIDs = zakenList.map { it.uuid },
                explanation = explanation,
                screenEventResourceId = screenEventResourceId
            )
            Then(
                """only the open zaak should no longer have a user assigned
                     but the group should still be assigned
                    and the search index should be updated and
                    a screen event of type 'zaken vrijgeven' should sent"""
            ) {
                verify(exactly = 1) {
                    zrcClientService.deleteRol(openZaak, BetrokkeneType.MEDEWERKER, explanation)
                }

                with(screenEventSlot.captured) {
                    opcode shouldBe Opcode.UPDATED
                    objectType shouldBe ScreenEventType.ZAKEN_VRIJGEVEN
                    objectId.resource shouldBe screenEventResourceId
                }
            }
        }
    }
    Given(
        """
            A list of zaken and a failing ZRC client service that throws an exception 
            when retrieving the second zaak 
            """
    ) {
        every { zrcClientService.readZaak(zaken[0].uuid) } returns zaken[0]
        every { zrcClientService.readZaak(zaken[1].uuid) } throws RuntimeException("dummyRuntimeException")
        When(
            """the assign zaken function is called with a group
                and a screen event resource id"""
        ) {
            shouldThrow<RuntimeException> {
                zaakService.assignZaken(
                    zaakUUIDs = zaken.map { it.uuid },
                    explanation = explanation,
                    group = group,
                    screenEventResourceId = screenEventResourceId
                )
            }
            Then(
                """the exception should be thrown and for neither of the zaken 
                    the group and user role nor the search index should be updated
                    and no screen event of type 'zaken verdelen' should be sent"""
            ) {
                verify(exactly = 0) {
                    zrcClientService.updateRol(any(), any(), explanation)
                    eventingService.send(any<ScreenEvent>())
                }
            }
        }
    }
    Given("A list of zaken") {
        val screenEventSlot = slot<ScreenEvent>()
        zaken.map {
            every { zrcClientService.readZaak(it.uuid) } returns it
            every {
                ztcClientService.readRoltype(
                    it.zaaktype,
                    OmschrijvingGeneriekEnum.BEHANDELAAR
                )
            } returns rolTypeBehandelaar
            every { zrcClientService.updateRol(it, any(), explanation) } just Runs
            every { zrcClientService.deleteRol(it, any(), explanation) } just Runs
            every { eventingService.send(capture(screenEventSlot)) } just Runs
        }
        When(
            """the assign zaken function is called with a group, WITHOUT a user
                and with a screen event resource id"""
        ) {
            zaakService.assignZaken(
                zaakUUIDs = zaken.map { it.uuid },
                explanation = explanation,
                group = group,
                screenEventResourceId = screenEventResourceId
            )

            Then(
                """for both zaken the group roles should be updated
                    and the user roles should be deleted"""
            ) {
                zaken.map {
                    verify(exactly = 1) {
                        zrcClientService.updateRol(it, any(), explanation)
                        zrcClientService.deleteRol(it, any(), explanation)
                    }
                }
            }
        }
    }
})
