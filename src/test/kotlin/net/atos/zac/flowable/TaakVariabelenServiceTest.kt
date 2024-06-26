package net.atos.zac.flowable

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import io.kotest.matchers.string.contain
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.flowable.common.engine.api.scope.ScopeTypes
import org.flowable.task.api.TaskInfo
import java.io.File
import java.util.UUID

class TaakVariabelenServiceTest : BehaviorSpec() {
    private val taskInfo = mockk<TaskInfo>()

    override suspend fun beforeContainer(testCase: TestCase) {
        super.beforeContainer(testCase)

        // Only run before Given
        if (testCase.parent != null) return

        MockKAnnotations.init(this)
        clearAllMocks()
    }

    init {
        Given("Task with correct zaak UUID object") {
            val service = TaakVariabelenService()
            val expectedUUID = UUID.fromString("e58ed763-928c-4155-bee9-fdbaaadc15f3")

            every { taskInfo.scopeType } returns ScopeTypes.CMMN
            every { taskInfo.caseVariables } returns mapOf(ZaakVariabelenService.VAR_ZAAK_UUID to expectedUUID)

            When("reading the zaak UUID") {
                val uuid = service.readZaakUUID(taskInfo)

                Then("it returns the right information") {
                    uuid shouldBeEqual expectedUUID
                }
            }
        }

        Given("Task with zaak UUID as string") {
            val service = TaakVariabelenService()
            val expectedUUID = "e58ed763-928c-4155-bee9-fdbaaadc15f3"

            every { taskInfo.scopeType } returns ScopeTypes.CMMN
            every { taskInfo.caseVariables } returns mapOf(ZaakVariabelenService.VAR_ZAAK_UUID to expectedUUID)

            When("reading the zaak UUID") {
                val exception = shouldThrow<ClassCastException> {
                    service.readZaakUUID(taskInfo)
                }

                Then("it throws an exception") {
                    exception.message should contain("java.lang.String cannot be cast to class java.util.UUID")
                }
            }
        }

        Given("Task with zaak UUID as unknown object") {
            val service = TaakVariabelenService()
            val expectedUUID = File("e58ed763-928c-4155-bee9-fdbaaadc15f3")

            every { taskInfo.scopeType } returns ScopeTypes.CMMN
            every { taskInfo.caseVariables } returns mapOf(ZaakVariabelenService.VAR_ZAAK_UUID to expectedUUID)

            When("reading the zaak UUID") {
                val exception = shouldThrow<ClassCastException> {
                    service.readZaakUUID(taskInfo)
                }

                Then("it throws an exception") {
                    exception.message should contain("java.io.File cannot be cast to class java.util.UUID")
                }
            }
        }

        Given("Task with correct zaak type UUID object") {
            val service = TaakVariabelenService()
            val expectedUUID = UUID.fromString("e58ed763-928c-4155-bee9-fdbaaadc15f3")

            every { taskInfo.scopeType } returns ScopeTypes.CMMN
            every { taskInfo.caseVariables } returns mapOf(ZaakVariabelenService.VAR_ZAAKTYPE_UUUID to expectedUUID)

            When("reading the zaak type UUID") {
                val uuid = service.readZaaktypeUUID(taskInfo)

                Then("it returns the right information") {
                    uuid shouldBeEqual expectedUUID
                }
            }
        }

        Given("Task with zaak type UUID as string") {
            val service = TaakVariabelenService()
            val expectedUUID = "e58ed763-928c-4155-bee9-fdbaaadc15f3"

            every { taskInfo.scopeType } returns ScopeTypes.CMMN
            every { taskInfo.caseVariables } returns mapOf(ZaakVariabelenService.VAR_ZAAKTYPE_UUUID to expectedUUID)

            When("reading the zaak type UUID") {
                val exception = shouldThrow<ClassCastException> {
                    service.readZaaktypeUUID(taskInfo)
                }

                Then("it throws an exception") {
                    exception.message should contain("java.lang.String cannot be cast to class java.util.UUID")
                }
            }
        }

        Given("Task with zaak type UUID as unknown object") {
            val service = TaakVariabelenService()
            val expectedUUID = File("e58ed763-928c-4155-bee9-fdbaaadc15f3")

            every { taskInfo.scopeType } returns ScopeTypes.CMMN
            every { taskInfo.caseVariables } returns mapOf(ZaakVariabelenService.VAR_ZAAKTYPE_UUUID to expectedUUID)

            When("reading the zaak type UUID") {
                val exception = shouldThrow<ClassCastException> {
                    service.readZaaktypeUUID(taskInfo)
                }

                Then("it throws an exception") {
                    exception.message should contain("java.io.File cannot be cast to class java.util.UUID")
                }
            }
        }
    }
}
