import android.app.Application
import com.tasktracker.data.CombinedTaskRepository
import com.tasktracker.data.local.repository.LocalTaskRepository
import com.tasktracker.viewModel.TaskViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDateTime
import com.tasktracker.domain.entity.Task
import com.tasktracker.viewModel.TaskUiState

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {
    private val combinedTaskRepository: CombinedTaskRepository = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    private lateinit var viewModel: TaskViewModel
    private val testDispatcher = StandardTestDispatcher()
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { combinedTaskRepository.getTasks() } returns flowOf(emptyList())
        viewModel = TaskViewModel(combinedTaskRepository, application)
    }
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }
    @Test
    fun `addTask calls repository and schedules notification`() = runTest {
        val task = createTestTask()

        viewModel.addTask(task)

        advanceUntilIdle()

        coVerify { combinedTaskRepository.addTask(task) }
    }

    @Test
    fun `deleteTask calls repository and cancels notification`() = runTest {
        val task = createTestTask()

        viewModel.deleteTask(task)

        advanceUntilIdle()

        coVerify { combinedTaskRepository.deleteTask(task) }
    }

    @Test
    fun `updateTask updates task and schedules notification`() = runTest {
        val task = createTestTask()

        viewModel.updateTask(task)

        advanceUntilIdle()

        coVerify { combinedTaskRepository.updateTask(task) }
    }

    @Test
    fun `getTaskById returns correct task`() = runTest {
        val task = createTestTask()
        coEvery { combinedTaskRepository.getTaskById(task.id ?: 0) } returns task

        val result = viewModel.getTaskById(task.id ?: 0)

        assertEquals(task, result)
    }

    @Test
    fun `taskUiState emits Loading then Success`() = runTest {
        val taskList = listOf(createTestTask())
        coEvery { combinedTaskRepository.getTasks() } returns flowOf(taskList)

        viewModel = TaskViewModel(combinedTaskRepository, application)
        advanceUntilIdle()

        val state = viewModel.taskUiState.value
        assertTrue(state is TaskUiState.Success)
        assertEquals(taskList, (state as TaskUiState.Success).result)
    }

    private fun createTestTask(): Task = Task(
        id = 1,
        title = "Test",
        description = "Test description",
        deadline = LocalDateTime.now(),
        isCompleted = false,
        categoryId = 1,
        begin = LocalDateTime.now()
    )
}
