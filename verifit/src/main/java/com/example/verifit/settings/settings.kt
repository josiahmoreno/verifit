package com.example.verifit.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.example.verifit.KnownExerciseServiceSingleton
import com.example.verifit.WorkoutServiceSingleton
import com.example.verifit.common.YesNoDialog
import com.example.verifit.main.getActivity
import com.example.verifit.workoutservice.WorkoutService
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import javax.inject.Inject

@ExperimentalComposeUiApi
class Compose_SettingsActivity() : AppCompatActivity() {
    // Helper Data Structure
    private val PermissionRequester = WritePermissionChecker.PermissionRequester(requestCallback = ActivityResultContracts.RequestPermission())
    val requestMultiplePermissions: ActivityResultLauncher<String> =
        registerForActivityResult(
            PermissionRequester.requestCallback, PermissionRequester.requestResult
        )

    private val callback : ActivityResultCallback<ActivityResult?> = ActivityResultCallback<ActivityResult?> {
        if (it.resultCode == Activity.RESULT_OK)
            ResultLauncherWrapper.resultInvoke?.invoke(it)
    }
    val requestFile: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ,callback)
    private val ResultLauncherWrapper =  ImportDataUseCase.ResultLauncherWrapper(launcher = requestFile)

    private val createFilecallback : ActivityResultCallback<Uri?> = ActivityResultCallback<Uri?> { uri ->
            if(uri != null)
            CreateFileLauncherWrapper.resultInvoke?.invoke(uri)
    }
    val createFileFile: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.CreateDocument()
            ,createFilecallback)
    private val CreateFileLauncherWrapper =  ExportDataUseCase.CreateDocumentLauncherWrapper(launcher = createFileFile)

    @Inject
    lateinit var viewModel: SettingsViewModel
    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppCompatTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            backgroundColor = MaterialTheme.colors.primary,
                            title = {

                                Text(text = "Settings",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis) // titl

                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    getActivity()?.onBackPressed()
                                    //viewModel.onAction(MviViewModel.UiAction.ShowComments)
                                    //viewModel.onAction(UiAction.ExitSearch)
                                }) {
                                    Icon(Icons.Filled.ArrowBack, "Back")
                                }
                            }
                        )
                    },
                    content = {
                        SettingsScreen(viewModel = viewModel)
                    },
                    bottomBar = {

                    }
                )


            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    //val showImportDialog = remember { mutableStateOf(false) }
    //val showDeleteDataDialog = remember { mutableStateOf(false) }
    val state = viewModel.viewState.collectAsState()
    val context = LocalContext.current
    Column() {
        Text(text = "Backup and Restore",
            modifier = Modifier.padding(start = 64.dp, top = 24.dp, bottom = 12.dp),
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
        //SettingsGroup(modifier = Modifier,title = {Text(text = "Backup and Restore")}) {
        SettingsMenuLink(
            title = { Text(text = "Import Workout Data") },
            subtitle = { Text(text = "Import workout data from a previous backup") },
            onClick = {
                //showImportDialog.value = true
                      viewModel.onAction(UiAction.OnImportMenuCLick)
            },
        )
        SettingsMenuLink(
            title = { Text(text = "Export Workout Data") },
            subtitle = { Text(text = "Create a backup of your ") },
            onClick = {
                viewModel.onAction(UiAction.OnExportMenuCLick)
            },
        )
        SettingsMenuLink(
            title = { Text(text = "Delete Data") },
            subtitle = { Text(text = "Delete all local workout data") },
            onClick = {
                viewModel.onAction(UiAction.DeleteDataCLick)
            },
        )
        if (state.value.showImportDialog.value) {
            YesNoDialog(state.value.showImportDialog, {
                viewModel.onAction(UiAction.YesImportData)
            }, {

            }, "This will overwrite all saved data")
        }
        if (state.value.showDeleteDialog.value) {
            YesNoDialog(state.value.showDeleteDialog, {
                                                      viewModel.onAction(UiAction.YesDeleteAllData)
            }, {

            }, "Delete all saved data?")
        }


        //}
        Divider(thickness = 1.dp)
        Column {
            Text(text = "General",
                modifier = Modifier.padding(start = 64.dp, top = 24.dp, bottom = 12.dp),
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            SettingsMenuLink(
                title = { Text(text = "Source Code") },
                subtitle = { Text(text = "Contributions are always welcome") },
                onClick = {},
            )
            SettingsMenuLink(
                title = { Text(text = "Licence") },
                subtitle = { Text(text = "GNU General Public Licence, version 3") },
                onClick = {},
            )
        }
    }
}



@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview() {
    MaterialTheme {
//        SettingsScreen(viewModel = SettingsViewModel(
//
//        ))
    }
}

class SettingsViewModelFactory(
    val context: Context,
    val workoutService: WorkoutService,
    val toastMaker: ToastMaker,
    val writePermissionChecker: WritePermissionChecker,
    val externalStorageChecker: ExternalStorageChecker,
    val importDataUseCase: ImportDataUseCase,
    val createFileFile: ExportDataUseCase.CreateDocumentLauncherWrapper,
    val deleteAllDataUseCase: DeleteAllDataUseCase
    //val dateSelectStore: DateSelectStore,
    //val knownExerciseService: KnownExerciseService,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            ExportDataUseCase = ExportDataUseCase(context = context,workoutService = workoutService,toastMaker,writePermissionChecker,externalStorageChecker, createFileFile),
                    importDataUseCase,
            deleteAllDataUseCase
            //FetchChartsDataUseCase = FetchChartsDataUseCaseImpl(workoutService)
            //FetchDaysWorkoutsUseCaseImpl(workoutService,dateSelectStore,knownExerciseService,colorGetter = ColorGetterImpl(knownExerciseService))
        )
                as T
    }
}
