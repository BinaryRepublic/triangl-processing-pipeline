package controller

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.times
import com.triangl.processing.controller.RepositoryController
import com.triangl.processing.outputEntity.*
import com.triangl.processing.repository.RepositoryExecutor
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import testSupport.OutputOperations

@RunWith(MockitoJUnitRunner::class)
class RepositoryControllerTest {

    @Mock
    private lateinit var repositoryExecutor: RepositoryExecutor

    private val outputOperations= OutputOperations()

    @Test
    fun `should APPLY in the right order`() {
        // given
        val outputOperation = outputOperations.customerOutputOperation()
        val repositoryController = RepositoryController(outputOperation, repositoryExecutor)

        // when
        repositoryController.applyOutputOperations()

        // then
        inOrder (repositoryExecutor) {
            verify(repositoryExecutor, times(1)).run(
                eq(outputOperation),
                eq("Customer"),
                eq(CustomerOutput::class.java)
            )
            verify(repositoryExecutor, times(1)).run(
                eq(outputOperation.children[0]),
                eq("Map"),
                eq(MapOutput::class.java)
            )
            verify(repositoryExecutor, times(1)).run(
                eq(outputOperation.children[0].children[0]),
                eq("Router"),
                eq(RouterOutput::class.java)
            )
            verify(repositoryExecutor, times(1)).run(
                eq(outputOperation.children[0].children[0].children[0]),
                eq("Coordinate"),
                eq(CoordinateOutput::class.java)
            )
            verify(repositoryExecutor, times(1)).run(
                eq(outputOperation.children[0].children[1]),
                eq("Area"),
                eq(AreaOutput::class.java)
            )
            verify(repositoryExecutor, times(1)).run(
                eq(outputOperation.children[0].children[1].children[0]),
                eq("Coordinate"),
                eq(CoordinateOutput::class.java)
            )
        }
    }

    @Test
    fun `should DELETE in the right order`() {
        // given
        val outputOperation = outputOperations.customerOutputOperation(true)
        val repositoryController = RepositoryController(outputOperation, repositoryExecutor)

        // when
        repositoryController.applyOutputOperations()

        // then
        inOrder (repositoryExecutor) {
            verify(repositoryExecutor, times(1)).run(
                    eq(outputOperation.children[0].children[0].children[0]),
                    eq("Coordinate"),
                    eq(CoordinateOutput::class.java)
            )
            verify(repositoryExecutor, times(1)).run(
                    eq(outputOperation.children[0].children[0]),
                    eq("Router"),
                    eq(RouterOutput::class.java)
            )
            verify(repositoryExecutor, times(1)).run(
                    eq(outputOperation.children[0].children[1].children[0]),
                    eq("Coordinate"),
                    eq(CoordinateOutput::class.java)
            )
            verify(repositoryExecutor, times(1)).run(
                    eq(outputOperation.children[0].children[1]),
                    eq("Area"),
                    eq(AreaOutput::class.java)
            )
            verify(repositoryExecutor, times(1)).run(
                    eq(outputOperation.children[0]),
                    eq("Map"),
                    eq(MapOutput::class.java)
            )
            verify(repositoryExecutor, times(1)).run(
                    eq(outputOperation),
                    eq("Customer"),
                    eq(CustomerOutput::class.java)
            )
        }
    }
}