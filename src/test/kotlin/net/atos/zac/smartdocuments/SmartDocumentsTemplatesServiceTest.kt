/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.smartdocuments

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.checkUnnecessaryStub
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityManager
import jakarta.persistence.Tuple
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import net.atos.client.smartdocuments.model.createsmartDocumentsTemplatesResponse
import net.atos.zac.admin.ZaakafhandelParameterService
import net.atos.zac.admin.model.ZaakafhandelParameters
import net.atos.zac.smartdocuments.templates.model.SmartDocumentsTemplate
import net.atos.zac.smartdocuments.templates.model.SmartDocumentsTemplateGroup
import java.util.UUID

class SmartDocumentsTemplatesServiceTest : BehaviorSpec({
    val entityManager = mockk<EntityManager>()
    val smartDocumentsService = mockk<SmartDocumentsService>()
    val zaakafhandelParameterService = mockk<ZaakafhandelParameterService>()
    val smartDocumentsTemplatesService = SmartDocumentsTemplatesService(
        entityManager = entityManager,
        smartDocumentsService = smartDocumentsService,
        zaakafhandelParameterService = zaakafhandelParameterService
    )

    beforeEach {
        checkUnnecessaryStub()
    }

    Given("A list of SmartDocuments templates") {
        val smartDocumentsTemplatesResponse = createsmartDocumentsTemplatesResponse()
        every { smartDocumentsService.listTemplates() } returns smartDocumentsTemplatesResponse

        When("a list of template is requested") {
            val restSmartDocumentsTemplateGroupSet = smartDocumentsTemplatesService.listTemplates()

            Then("the template is returned") {
                restSmartDocumentsTemplateGroupSet.size shouldBe
                    smartDocumentsTemplatesResponse.documentsStructure.templatesStructure.templateGroups.size
            }
        }
    }

    Given("A missing mapping") {
        val zaakafhandelParametersUUID = UUID.randomUUID()
        val zaakafhanderParametersId = 1L
        val templateGroupId = "template group id"
        val templateId = "template id"

        val criteriaBuilder = mockk<CriteriaBuilder>()
        val criteriaQuery = mockk<CriteriaQuery<Tuple>>()
        val root = mockk<Root<SmartDocumentsTemplate>>()
        val namePath = mockk<Path<UUID>>()
        val zaakafhandelParametersPath = mockk<Path<ZaakafhandelParameters>>()
        val longPath = mockk<Path<Long>>()
        val stringPath = mockk<Path<String>>()
        val templateGroupPath = mockk<Path<SmartDocumentsTemplateGroup>>()
        val templatePath = mockk<Path<SmartDocumentsTemplate>>()
        val predicate = mockk<Predicate>()
        val zaakafhandelParameters = mockk<ZaakafhandelParameters>()
        val typedQuery = mockk<TypedQuery<Tuple>>()
        val tuple = mockk<Tuple>()

        every { entityManager.criteriaBuilder } returns criteriaBuilder
        every { entityManager.createQuery(criteriaQuery) } returns typedQuery

        every { criteriaBuilder.createTupleQuery() } returns criteriaQuery
        every { criteriaBuilder.and(any<Predicate>(), any<Predicate>(), any<Predicate>()) } returns predicate
        every { criteriaBuilder.equal(longPath, zaakafhanderParametersId) } returns predicate
        every { criteriaBuilder.equal(stringPath, templateGroupId) } returns predicate
        every { criteriaBuilder.equal(templatePath, templateId) } returns predicate

        every { criteriaQuery.multiselect(namePath) } returns criteriaQuery
        every { criteriaQuery.where(any<Predicate>()) } returns criteriaQuery
        every { criteriaQuery.from(SmartDocumentsTemplate::class.java) } returns root

        every { root.get<UUID>("informatieObjectTypeUUID") } returns namePath
        every { root.get<ZaakafhandelParameters>("zaakafhandelParameters") } returns zaakafhandelParametersPath
        every { root.get<SmartDocumentsTemplateGroup>("templateGroup") } returns templateGroupPath
        every { root.get<SmartDocumentsTemplate>("smartDocumentsId") } returns templatePath

        every { zaakafhandelParametersPath.get<Long>("id") } returns longPath
        every { templateGroupPath.get<String>("smartDocumentsId") } returns stringPath

        every {
            zaakafhandelParameterService.readZaakafhandelParameters((zaakafhandelParametersUUID))
        } returns zaakafhandelParameters
        every { zaakafhandelParameters.id } returns zaakafhanderParametersId

        every { typedQuery.setMaxResults(any<Int>()) } returns typedQuery
        every { typedQuery.resultList } returns listOf(tuple)

        every { tuple.get(namePath) } returns null

        When("information object UUID is requested") {
            val exception = shouldThrow<SmartDocumentsException> {
                smartDocumentsTemplatesService.getInformationObjectTypeUUID(
                    zaakafhandelParametersUUID,
                    templateGroupId,
                    templateId
                )
            }

            Then("exception is thrown") {
                exception.message shouldContain templateGroupId
                exception.message shouldContain templateId
            }
        }
    }
})
