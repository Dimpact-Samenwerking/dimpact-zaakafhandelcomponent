package nl.lifely.zac.itest

import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.json.shouldContainJsonKey
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldNotContainJsonKey
import io.kotest.core.spec.Order
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import nl.lifely.zac.itest.client.ItestHttpClient
import nl.lifely.zac.itest.client.ZacClient
import nl.lifely.zac.itest.config.ItestConfiguration.ACTIE_INTAKE_AFRONDEN
import nl.lifely.zac.itest.config.ItestConfiguration.ACTIE_ZAAK_AFHANDELEN
import nl.lifely.zac.itest.config.ItestConfiguration.TEST_GROUP_A_DESCRIPTION
import nl.lifely.zac.itest.config.ItestConfiguration.TEST_GROUP_A_ID
import nl.lifely.zac.itest.config.ItestConfiguration.TEST_SPEC_ORDER_AFTER_ZAAK_UPDATED
import nl.lifely.zac.itest.config.ItestConfiguration.ZAAKTYPE_MELDING_KLEIN_EVENEMENT_UUID
import nl.lifely.zac.itest.config.ItestConfiguration.ZAC_API_URI
import org.json.JSONArray
import org.json.JSONObject
import org.mockserver.model.HttpStatusCode
import java.util.UUID

const val ONE_SECOND_IN_MILLIS = 1000L

/**
 * This test creates a zaak, adds a task to complete the intake phase, closes the zaak, and then re-opens the zaak.
 */
@Order(TEST_SPEC_ORDER_AFTER_ZAAK_UPDATED)
class ZakenRESTServiceCompleteTest : BehaviorSpec({
    val itestHttpClient = ItestHttpClient()
    val zacClient = ZacClient()
    val logger = KotlinLogging.logger {}

    Given("A zaak has been created that has finished the intake phase with the status 'admissible'") {
        lateinit var zaakUUID: UUID
        lateinit var resultaatTypeUuid: UUID
        val intakeId: Int
        zacClient.createZaak(
            ZAAKTYPE_MELDING_KLEIN_EVENEMENT_UUID,
            TEST_GROUP_A_ID,
            TEST_GROUP_A_DESCRIPTION
        ).run {
            JSONObject(body!!.string()).run {
                getJSONObject("zaakdata").run {
                    zaakUUID = getString("zaakUUID").run(UUID::fromString)
                }
            }
        }
        itestHttpClient.performGetRequest(
            "$ZAC_API_URI/planitems/zaak/$zaakUUID/userEventListenerPlanItems"
        ).run {
            JSONArray(body!!.string()).getJSONObject(0).run {
                intakeId = getString("id").toInt()
            }
        }
        sleep(1)
        itestHttpClient.performJSONPostRequest(
            "$ZAC_API_URI/planitems/doUserEventListenerPlanItem",
            requestBodyAsString = """
            {
                "zaakUuid":"$zaakUUID",
                "planItemInstanceId":"$intakeId",
                "actie":"$ACTIE_INTAKE_AFRONDEN",
                "zaakOntvankelijk":true
            }
            """.trimIndent()
        ).run {
            logger.info { "Response: ${body!!.string()}" }
            code shouldBe HttpStatusCode.NO_CONTENT_204.code()
        }
        itestHttpClient.performGetRequest(
            "$ZAC_API_URI/zaken/resultaattypes/$ZAAKTYPE_MELDING_KLEIN_EVENEMENT_UUID"
        ).run {
            JSONArray(body!!.string()).getJSONObject(0).run {
                // we do not care about the specific result type so we just take the first one
                resultaatTypeUuid = getString("id").let(UUID::fromString)
            }
        }

        When("the zaak is completed") {
            val afhandelenId: Int
            itestHttpClient.performGetRequest(
                "$ZAC_API_URI/planitems/zaak/$zaakUUID/userEventListenerPlanItems"
            ).run {
                JSONArray(body!!.string()).getJSONObject(0).run {
                    afhandelenId = getString("id").toInt()
                }
            }
            sleep(1)
            itestHttpClient.performJSONPostRequest(
                "$ZAC_API_URI/planitems/doUserEventListenerPlanItem",
                requestBodyAsString = """
            {
                "zaakUuid":"$zaakUUID",
                "planItemInstanceId":"$afhandelenId",
                "actie":"$ACTIE_ZAAK_AFHANDELEN",
                "resultaattypeUuid": "$resultaatTypeUuid",
                "resultaatToelichting":"afronden"
            }
                """.trimIndent()
            ).run {
                logger.info { "Response: ${body!!.string()}" }
                code shouldBe HttpStatusCode.NO_CONTENT_204.code()
            }

            Then("the zaak should be closed and have a result") {
                zacClient.retrieveZaak(zaakUUID).use { response ->
                    val responseBody = response.body!!.string()
                    logger.info { "Response: $responseBody" }
                    response.code shouldBe HttpStatusCode.OK_200.code()
                    responseBody.run {
                        shouldContainJsonKeyValue("isOpen", false)
                        shouldContainJsonKey("resultaat")
                    }
                }
            }
        }

        When("the closed zaak is re-opened") {
            sleep(1)
            itestHttpClient.performPatchRequest(
                "$ZAC_API_URI/zaken/zaak/$zaakUUID/heropenen",
                requestBodyAsString = """
                    {"reden":"dummyReason"}
                """.trimIndent()
            ).run {
                logger.info { "Response: ${body!!.string()}" }
                code shouldBe HttpStatusCode.NO_CONTENT_204.code()
            }

            Then("the zaak should be open and should no longer have a result") {
                zacClient.retrieveZaak(zaakUUID).use { response ->
                    val responseBody = response.body!!.string()
                    logger.info { "Response: $responseBody" }
                    response.code shouldBe HttpStatusCode.OK_200.code()
                    responseBody.run {
                        shouldContainJsonKeyValue("isOpen", true)
                        shouldNotContainJsonKey("resultaat")
                    }
                }
            }
        }

        When("the re-opened zaak is completed again") {
            sleep(1)
            // Completing a re-opened zaak is done using the 'afsluiten' endpoint
            // instead of the 'doUserEventListenerPlanItem' endpoint.
            // Not sure why.
            itestHttpClient.performPatchRequest(
                url = "$ZAC_API_URI/zaken/zaak/$zaakUUID/afsluiten",
                requestBodyAsString = """
                    {
                    "reden":"dummyReason",
                    "resultaattypeUuid":"$resultaatTypeUuid"
                    }
                """.trimIndent()
            ).run {
                logger.info { "Response: ${body!!.string()}" }
                code shouldBe HttpStatusCode.NO_CONTENT_204.code()
            }

            Then("the zaak should be closed and have a result") {
                zacClient.retrieveZaak(zaakUUID).use { response ->
                    val responseBody = response.body!!.string()
                    logger.info { "Response: $responseBody" }
                    response.code shouldBe HttpStatusCode.OK_200.code()
                    responseBody.run {
                        shouldContainJsonKeyValue("isOpen", false)
                        shouldContainJsonKey("resultaat")
                    }
                }
            }
        }
    }
})

/**
 * Wait before setting the status of a zaak (implicitly)
 * because OpenZaak does not allow setting multiple statuses for one zaak
 * within the same timeframe of one second.
 * If we do not wait in these cases we get a 400 response from OpenZaak with:
 * "rest_framework.exceptions.ValidationError: {'non_field_errors':
 * [ErrorDetail(string='De velden zaak, datum_status_gezet moeten een unieke set zijn.', code='unique')]}"
 *
 * Related OpenZaak issue: https://github.com/open-zaak/open-zaak/issues/1639
 */
fun sleep(seconds: Long) {
    Thread.sleep(seconds * ONE_SECOND_IN_MILLIS)
}
