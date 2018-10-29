package controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.triangl.processing.controller.ConverterController
import com.triangl.processing.converter.CustomerConverter
import com.triangl.processing.converter.TrackedDeviceConverter
import com.triangl.processing.dto.InputOperationTypeDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import testSupport.OutputOperations
import java.io.File
import java.io.FileReader

class ConverterControllerIntegrationTest {

    private val converterController = ConverterController(
        CustomerConverter(),
        TrackedDeviceConverter()
    )

    private fun getTestSupportJson(fileName: String): String {
        val path = File("src/test/kotlin/testSupport/$fileName").absolutePath
        return FileReader(path).readText()
    }

    private val outputOperations = OutputOperations()

    @Test
    fun `should generate output-operation on APPLY_CUSTOMER`() {
        // given
        val inputOperationType = InputOperationTypeDto.APPLY_CUSTOMER
        val jsonPayload = getTestSupportJson("PubSubCustomerInput.json")

        // when
        val resultOutputOperation = converterController.constructOutputOperations(inputOperationType, jsonPayload, null)

        // then
        val objectMapper = ObjectMapper()
        val resultOutputOperationString = objectMapper.writeValueAsString(resultOutputOperation)
        val expectedOutputOperationString = objectMapper.writeValueAsString(outputOperations.customerOutputOperation())
        assertThat(resultOutputOperationString).isEqualTo(expectedOutputOperationString)
    }

    @Test
    fun `should generate output-operation on APPLY_TRACKING_POINT`() {
        // given
        val inputOperationType = InputOperationTypeDto.APPLY_TRACKING_POINT
        val jsonPayload = getTestSupportJson("PubSubTrackingPointInput.json")

        // when
        val resultOutputOperation = converterController.constructOutputOperations(inputOperationType, jsonPayload, "{\"mapId\": \"m1\"}")

        // then
        val objectMapper = ObjectMapper()
        val resultOutputOperationString = objectMapper.writeValueAsString(resultOutputOperation)
        val expectedOutputOperationString = objectMapper.writeValueAsString(outputOperations.trackingPointOutputOperation())
        assertThat(resultOutputOperationString).isEqualTo(expectedOutputOperationString)
    }
}