/*
 * SPDX-FileCopyrightText: 2021 Atos, 2024 INFO.nl
 * SPDX-License-Identifier: EUPL-1.2+
 */
package nl.info.client.zgw.shared

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import net.atos.client.zgw.drc.DrcClientService
import net.atos.client.zgw.shared.util.DateTimeUtil.convertToDateTime
import net.atos.client.zgw.zrc.model.Rol
import net.atos.client.zgw.zrc.model.RolListParameters
import net.atos.client.zgw.zrc.model.RolMedewerker
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid
import net.atos.client.zgw.zrc.model.ZaakInformatieobject
import nl.info.client.zgw.drc.model.generated.EnkelvoudigInformatieObject
import nl.info.client.zgw.drc.model.generated.EnkelvoudigInformatieObjectCreateLockRequest
import nl.info.client.zgw.drc.model.generated.Gebruiksrechten
import nl.info.client.zgw.shared.exception.ResultTypeNotFoundException
import nl.info.client.zgw.shared.exception.StatusTypeNotFoundException
import nl.info.client.zgw.util.extractUuid
import nl.info.client.zgw.zrc.ZrcClientService
import nl.info.client.zgw.zrc.model.generated.BetrokkeneTypeEnum
import nl.info.client.zgw.zrc.model.generated.Resultaat
import nl.info.client.zgw.zrc.model.generated.Status
import nl.info.client.zgw.zrc.model.generated.Zaak
import nl.info.client.zgw.ztc.ZtcClientService
import nl.info.client.zgw.ztc.model.generated.AfleidingswijzeEnum
import nl.info.client.zgw.ztc.model.generated.OmschrijvingGeneriekEnum
import nl.info.client.zgw.ztc.model.generated.ResultaatType
import nl.info.client.zgw.ztc.model.generated.StatusType
import nl.info.zac.util.AllOpen
import nl.info.zac.util.NoArgConstructor
import org.apache.commons.lang3.StringUtils
import java.net.URI
import java.time.LocalDate
import java.time.Period
import java.time.ZonedDateTime
import java.util.UUID
import java.util.logging.Logger

/**
 * Service class for ZGW API's.
 */
@ApplicationScoped
@Suppress("TooManyFunctions")
@AllOpen
@NoArgConstructor
class ZGWApiService @Inject constructor(
    val ztcClientService: ZtcClientService,
    val zrcClientService: ZrcClientService,
    val drcClientService: DrcClientService
) {
    companion object {
        private val LOG: Logger = Logger.getLogger(ZGWApiService::class.java.getName())

        // Page numbering in ZGW APIs starts with 1
        const val FIRST_PAGE_NUMBER_ZGW_APIS: Int = 1
        const val ZAAK_OBJECT_DELETION_PREFIX = "Verwijderd"
    }

    /**
     * Creates a new [Zaak].
     *
     * @param zaak [Zaak]
     * @return Created [Zaak]
     */
    fun createZaak(zaak: Zaak): Zaak {
        calculateDoorlooptijden(zaak)
        return zrcClientService.createZaak(zaak)
    }

    /**
     * Create [Status] for a given [Zaak] based on [StatusType].omschrijving and with [Status].toelichting.
     *
     * @param zaak [Zaak]
     * @param statusTypeOmschrijving Omschrijving of the [StatusType] of the required [Status].
     * @param statusToelichting Toelichting for thew [Status].
     * @return Created [Status].
     */
    fun createStatusForZaak(
        zaak: Zaak,
        statusTypeOmschrijving: String,
        statusToelichting: String?
    ): Status {
        val statustype = readStatustype(
            statustypes = ztcClientService.readStatustypen(zaak.zaaktype),
            omschrijving = statusTypeOmschrijving,
            zaaktypeURI = zaak.zaaktype
        )
        return createStatusForZaak(zaak.url, statustype.url, statusToelichting)
    }

    /**
     * Create [Resultaat] for a given [Zaak] based on [ResultaatType] .omschrijving and with
     * [Resultaat].toelichting.
     *
     * @param zaak [Zaak]
     * @param resultaattypeOmschrijving Omschrijving of the [ResultaatType] of the required [Resultaat].
     * @param resultaatToelichting Toelichting for thew [Resultaat].
     */
    fun createResultaatForZaak(
        zaak: Zaak,
        resultaattypeOmschrijving: String,
        resultaatToelichting: String
    ) {
        val resultaattypen = ztcClientService.readResultaattypen(zaak.getZaaktype())
        val resultaattype = filterResultaattype(
            resultaattypen,
            resultaattypeOmschrijving,
            zaak.zaaktype
        )
        createResultaat(zaak.url, resultaattype.url, resultaatToelichting)
    }

    /**
     * Create [Resultaat] for a given [Zaak] based on [ResultaatType].UUID and with [Resultaat].toelichting.
     *
     * @param zaak [Zaak]
     * @param resultaattypeUUID UUID of the [ResultaatType] of the required [Resultaat].
     * @param resultaatToelichting Toelichting for thew [Resultaat].
     */
    fun createResultaatForZaak(
        zaak: Zaak,
        resultaattypeUUID: UUID,
        resultaatToelichting: String?
    ) {
        val resultaattype = ztcClientService.readResultaattype(resultaattypeUUID)
        createResultaat(zaak.url, resultaattype.url, resultaatToelichting)
    }

    /**
     * Update [Resultaat] for a given [Zaak] based on [ResultaatType].UUID and with [Resultaat] .toelichting.
     *
     * @param zaak [Zaak]
     * @param resultaatTypeUuid Containing the UUID of the [ResultaatType] of the required [Resultaat].
     * @param reden Reason of setting the [ResultaatType]
     */
    fun updateResultaatForZaak(zaak: Zaak, resultaatTypeUuid: UUID, reden: String?) {
        zaak.resultaat?.let {
            val resultaat = zrcClientService.readResultaat(it)
            zrcClientService.deleteResultaat(resultaat.uuid)
        }
        createResultaatForZaak(zaak, resultaatTypeUuid, reden)
    }

    /**
     * End [Zaak]. Creating a new Eind [Status] for the [Zaak]. And calculating the archiverings parameters
     *
     * @param zaak [Zaak]
     * @param eindstatusToelichting Toelichting for thew Eind [Status].
     */
    fun endZaak(zaak: Zaak, eindstatusToelichting: String) {
        closeZaak(zaak, eindstatusToelichting)
        berekenArchiveringsparameters(zaak.uuid)
    }

    /**
     * End [Zaak]. Creating a new Eind [Status] for the [Zaak]. And calculating the archiverings parameters
     *
     * @param zaakUUID UUID of the [Zaak]
     * @param eindstatusToelichting Toelichting for thew Eind [Status].
     */
    fun endZaak(zaakUUID: UUID, eindstatusToelichting: String) {
        val zaak = zrcClientService.readZaak(zaakUUID)
        endZaak(zaak, eindstatusToelichting)
    }

    /**
     * Close [Zaak]. Creating a new Eind [Status] for the [Zaak].
     *
     * @param zaak [Zaak] to be closed
     * @param eindstatusToelichting Toelichting for thew Eind [Status].
     */
    fun closeZaak(zaak: Zaak, eindstatusToelichting: String?) {
        val eindStatustype = readStatustypeEind(
            ztcClientService.readStatustypen(zaak.zaaktype),
            zaak.zaaktype
        )
        createStatusForZaak(zaak.url, eindStatustype.url, eindstatusToelichting)
    }

    /**
     * Create [EnkelvoudigInformatieObject] and [ZaakInformatieobject] for [Zaak].
     *
     * @param zaak [Zaak].
     * @param enkelvoudigInformatieObjectCreateLockRequest [EnkelvoudigInformatieObject] to be created.
     * @param titel Titel of the new [ZaakInformatieobject].
     * @param beschrijving Beschrijving of the new [ZaakInformatieobject].
     * @param omschrijvingVoorwaardenGebruiksrechten Used to create the [Gebruiksrechten] for the to be created
     * [EnkelvoudigInformatieObject]
     * @return Created [ZaakInformatieobject].
     */
    fun createZaakInformatieobjectForZaak(
        zaak: Zaak,
        enkelvoudigInformatieObjectCreateLockRequest: EnkelvoudigInformatieObjectCreateLockRequest,
        titel: String,
        beschrijving: String?,
        omschrijvingVoorwaardenGebruiksrechten: String?
    ): ZaakInformatieobject {
        val newInformatieObjectData = drcClientService.createEnkelvoudigInformatieobject(
            enkelvoudigInformatieObjectCreateLockRequest
        )
        // Gebruiksrechten are required for every created zaak-informatieobject or else
        // the zaak in question can no longer be aborted or closed (OpenZaak will return a 400 error on aborting or closing in that case).
        val gebruiksrechten = Gebruiksrechten().apply {
            informatieobject = newInformatieObjectData.url
            startdatum = convertToDateTime(newInformatieObjectData.creatiedatum).toOffsetDateTime()
            omschrijvingVoorwaarden = omschrijvingVoorwaardenGebruiksrechten
        }
        drcClientService.createGebruiksrechten(gebruiksrechten)

        val zaakInformatieObject = ZaakInformatieobject().apply {
            this.zaak = zaak.url
            informatieobject = newInformatieObjectData.url
            this.titel = titel
            this.beschrijving = beschrijving
        }
        return zrcClientService.createZaakInformatieobject(zaakInformatieObject, StringUtils.EMPTY)
    }

    /**
     * Delete [ZaakInformatieobject] which relates [EnkelvoudigInformatieObject] and [Zaak] with zaakUUID. When the
     * [EnkelvoudigInformatieObject] has no other related [ZaakInformatieobject]s then it is also deleted.
     *
     * @param enkelvoudigInformatieobject [EnkelvoudigInformatieObject]
     * @param zaakUUID UUID of a [Zaak]
     * @param toelichting Explanation why the [EnkelvoudigInformatieObject] is to be removed; may be null.
     */
    fun removeEnkelvoudigInformatieObjectFromZaak(
        enkelvoudigInformatieobject: EnkelvoudigInformatieObject,
        zaakUUID: UUID,
        toelichting: String?
    ) {
        val zaakInformatieobjecten = zrcClientService.listZaakinformatieobjecten(
            enkelvoudigInformatieobject
        )
        // delete the relationship of the EnkelvoudigInformatieobject with the zaak.
        zaakInformatieobjecten
            .filter { it.zaakUUID == zaakUUID }
            .forEach { zrcClientService.deleteZaakInformatieobject(it.uuid, toelichting, ZAAK_OBJECT_DELETION_PREFIX) }

        // if the EnkelvoudigInformatieobject has no relationship(s) with other zaken it can be deleted.
        if (zaakInformatieobjecten.all { it.zaakUUID == zaakUUID }) {
            drcClientService.deleteEnkelvoudigInformatieobject(enkelvoudigInformatieobject.url.extractUuid())
        }
    }

    /**
     * Find [RolOrganisatorischeEenheid] for [Zaak] with initiator [OmschrijvingGeneriekEnum].
     *
     * @param zaak [Zaak].
     * @return [RolOrganisatorischeEenheid] or 'null'.
     */
    fun findGroepForZaak(zaak: Zaak): RolOrganisatorischeEenheid? =
        findBehandelaarRoleForZaak(zaak, BetrokkeneTypeEnum.ORGANISATORISCHE_EENHEID)?.let {
            it as RolOrganisatorischeEenheid
        }

    /**
     * Find [RolMedewerker] for [Zaak] with initiator [OmschrijvingGeneriekEnum].
     *
     * @param zaak [Zaak]
     * @return [RolMedewerker] or 'null' if the rol medewerker could not be found.
     */
    fun findBehandelaarMedewerkerRoleForZaak(zaak: Zaak): RolMedewerker? =
        findBehandelaarRoleForZaak(zaak, BetrokkeneTypeEnum.MEDEWERKER)?.let {
            it as RolMedewerker
        }

    fun findInitiatorRoleForZaak(zaak: Zaak): Rol<*>? {
        val roleTypes = ztcClientService.findRoltypen(zaak.zaaktype, OmschrijvingGeneriekEnum.INITIATOR).also {
            if (it.size > 1) {
                LOG.warning(
                    "Multiple initiator role types found for zaaktype: '${zaak.zaaktype}', using the first one."
                )
            }
        }
        return roleTypes.firstOrNull()?.let { rolType ->
            val roles = zrcClientService.listRollen(RolListParameters(zaak.url, rolType.url)).results.also {
                check(it.size <= 1) {
                    "More than one initiator role found for zaak with UUID: '${zaak.uuid}' (count: ${it.size})"
                }
            }
            roles.firstOrNull()
        }
    }

    private fun findBehandelaarRoleForZaak(
        zaak: Zaak,
        betrokkeneType: BetrokkeneTypeEnum
    ): Rol<*>? {
        val roleTypes = ztcClientService.findRoltypen(zaak.zaaktype, OmschrijvingGeneriekEnum.BEHANDELAAR).also {
            if (it.size > 1) {
                LOG.warning(
                    "Multiple behandelaar role types found for zaaktype: '${zaak.zaaktype}', using the first one."
                )
            }
        }
        return roleTypes.firstOrNull()?.let { roleType ->
            val roles = zrcClientService.listRollen(
                RolListParameters(zaak.url, roleType.url, betrokkeneType)
            ).results.also {
                check(it.size <= 1) {
                    "More than one behandelaar role found for zaak with UUID: '${zaak.uuid}' (count: ${it.size})"
                }
            }
            roles.firstOrNull()
        }
    }

    private fun createStatusForZaak(zaakURI: URI, statustypeURI: URI, toelichting: String?): Status {
        val status = Status().apply {
            zaak = zaakURI
            statustype = statustypeURI
            datumStatusGezet = ZonedDateTime.now().toOffsetDateTime()
        }
        status.statustoelichting = toelichting
        return zrcClientService.createStatus(status)
    }

    private fun calculateDoorlooptijden(zaak: Zaak) {
        val zaaktype = ztcClientService.readZaaktype(zaak.zaaktype)
        zaaktype.servicenorm?.let {
            zaak.einddatumGepland = zaak.startdatum.plus(Period.parse(it))
        }
        zaak.uiterlijkeEinddatumAfdoening = zaak.startdatum.plus(Period.parse(zaaktype.doorlooptijd))
    }

    private fun createResultaat(
        zaakURI: URI,
        resultaattypeURI: URI,
        resultaatToelichting: String?
    ) =
        zrcClientService.createResultaat(
            Resultaat().apply {
                zaak = zaakURI
                resultaattype = resultaattypeURI
                toelichting = resultaatToelichting
            }
        )

    private fun filterResultaattype(
        resultaattypes: List<ResultaatType>,
        description: String,
        zaaktypeURI: URI
    ): ResultaatType = resultaattypes
        .firstOrNull { it.omschrijving == description }
        ?: throw ResultTypeNotFoundException(
            "Resultaattype with description '$description' not found for zaaktype with URI: '$zaaktypeURI'."
        )

    private fun berekenArchiveringsparameters(zaakUUID: UUID) {
        val zaak = zrcClientService.readZaak(zaakUUID)
        // refetch to get the einddatum (the archiefnominatie has also been set)
        val resultaattype = ztcClientService.readResultaattype(
            zrcClientService.readResultaat(zaak.resultaat).resultaattype
        )
        resultaattype.archiefactietermijn?.let {
            // no idea what it means when there is no archiefactietermijn
            bepaalBrondatum(zaak, resultaattype)?.let { brondatum ->
                val zaakPatch = Zaak().apply {
                    archiefactiedatum = brondatum.plus(Period.parse(it))
                }
                zrcClientService.patchZaak(zaakUUID, zaakPatch)
            }
        }
    }

    private fun bepaalBrondatum(zaak: Zaak, resultaattype: ResultaatType): LocalDate? {
        val brondatumArchiefprocedure = resultaattype.brondatumArchiefprocedure ?: return null
        return if (brondatumArchiefprocedure.afleidingswijze == AfleidingswijzeEnum.AFGEHANDELD) {
            zaak.einddatum
        } else {
            LOG.warning(
                "Determining the 'brondatum' for 'afleidingswijze' " +
                    "'${brondatumArchiefprocedure.afleidingswijze}' is not supported"
            )
            null
        }
    }

    private fun readStatustype(
        statustypes: List<StatusType>,
        omschrijving: String,
        zaaktypeURI: URI
    ): StatusType = statustypes
        .firstOrNull { omschrijving == it.omschrijving }
        ?: throw StatusTypeNotFoundException(
            "Status type with description '$omschrijving' not found for zaaktype with URI: '$zaaktypeURI'."
        )

    private fun readStatustypeEind(
        statustypes: List<StatusType>,
        zaaktypeURI: URI
    ): StatusType = statustypes
        .firstOrNull { it.isEindstatus }
        ?: throw StatusTypeNotFoundException(
            "No status type with 'end state' found for zaaktype with URI: '$zaaktypeURI'."
        )
}
