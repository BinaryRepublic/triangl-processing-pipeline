package controller

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.triangl.processing.controller.ConverterController
import com.triangl.processing.converter.CustomerConverter
import com.triangl.processing.converter.TrackedDeviceConverter
import com.triangl.processing.dto.InputOperationTypeDto
import com.triangl.processing.inputEntity.CustomerInput
import com.triangl.processing.inputEntity.TrackingPointInput
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyListOf
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import testSupport.OutputOperations
import java.io.File
import java.io.FileReader

@RunWith(MockitoJUnitRunner::class)
class ConverterControllerTest {

    @Mock
    private lateinit var customerConverter: CustomerConverter

    @Mock
    private lateinit var trackedDeviceConverter: TrackedDeviceConverter

    @InjectMocks
    private lateinit var converterController: ConverterController

    private val outputOperations = OutputOperations()

    private fun getTestSupportJson(fileName: String): String {
        val path = File("src/test/kotlin/testSupport/$fileName").absolutePath
        return FileReader(path).readText()
    }

    @Test
    fun `should construct output operation for customer input`() {
        // given
        val inputOperationType = InputOperationTypeDto.APPLY_CUSTOMER
        val jsonPayload = getTestSupportJson("PubSubCustomerInput.json")
        val resultOutputOperation = outputOperations.customerOutputOperation()

        given(customerConverter.apply(anyListOf(CustomerInput::class.java))).willReturn(resultOutputOperation)

        // when
        val outputOperations = converterController.constructOutputOperations(inputOperationType, jsonPayload, null)

        // then
        assertThat(outputOperations).isEqualTo(resultOutputOperation)
    }

    @Test
    fun `should construct output operation for tracking-point input`() {
        // given
        val inputOperationType = InputOperationTypeDto.APPLY_TRACKING_POINT
        val jsonPayload = getTestSupportJson("PubSubTrackingPointInput.json")
        val additional = "{\"mapId\": \"m1\"}"
        val resultOutputOperation = outputOperations.trackingPointOutputOperation()

        given(trackedDeviceConverter.apply(anyListOf(TrackingPointInput::class.java), eq("m1"))).willReturn(resultOutputOperation)

        // when
        val outputOperations = converterController.constructOutputOperations(inputOperationType, jsonPayload, additional)

        // then
        assertThat(outputOperations).isEqualTo(resultOutputOperation)
    }
}