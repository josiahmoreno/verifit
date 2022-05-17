package com.example.verifit.settings

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import com.example.verifit.workoutservice.WorkoutService
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Paths

class ExportDataUseCase(
    private val context: Context,
    private val workoutService: WorkoutService,
    private val toastMaker: ToastMaker,
//    private val writePermissionChecker: WritePermissionChecker,
//    private val externalStorageChecker: ExternalStorageChecker,
    private val createDocumentLauncherWrapper: CreateDocumentLauncherWrapper,
) {
    var EXPORT_FILENAME = "verifit_backup"
    
    suspend operator fun invoke() = createDocument()

    private suspend fun createDocument(){
//        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.type = "text/*"
//        intent.putExtra(Intent.EXTRA_TITLE, "textfile.")
        createDocumentLauncherWrapper.resultInvoke = {
//            runBlocking {
//                pleaseRun(it)
//            }
            pleaseRun(it)
        }

        createDocumentLauncherWrapper.launcher.launch("$EXPORT_FILENAME")
    }
    private  fun pleaseRun(uri: Uri) {
        //val ex = externalStorageChecker()
        //val write = writePermissionChecker()
        //if (ex && write) {
                // The folder where everything is stored
                val verifit_folder : String = "verifit"

                // Print Root Directory (sanity check)
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.toPath()
                //context.filesDir.toPath()

            //context.getExternalFilesDir(null)
                println(dir)

                // Create verifit path
                var path = dir
                path = Paths.get(path.toString() + verifit_folder)

//                // If verifit path doesn't exist create it
//                if (Files.exists(path)) {
//                    println("This path exists")
//                } else {
//                    println("This path does not exist")
//                    val folder = File( dir
//                        .toString() + File.separator + verifit_folder)
//                    var success = true
//                    if (!folder.exists()) {
//                        success = folder.mkdirs()
//                    }
//                    if (success) {
//                        println("Verifit folder has been created")
//                    } else {
//                        println("Verifit folder has not been created")
//                    }
//                }

                // Write file in the verifit path
//                val textfile = File( dir
//                    .toString() + File.separator + verifit_folder, MainActivity.EXPORT_FILENAME)

                try {
                    val workoutDays = workoutService.fetchWorkoutDays()
                    val foss: OutputStream? = context.contentResolver.openOutputStream(uri)
                    val fos = foss
                    fos?.write("Date,Exercise,Category,Weight (kg),Reps,Comment\n".toByteArray())
                    for (i in workoutDays.indices) {
                        for (j in workoutDays[i].exercises.indices) {
                            val exerciseComment = workoutDays[i].exercises[j].comment
                            for (k in workoutDays[i].exercises[j].sets.indices) {
                                val Date = workoutDays[i].exercises[j].date
                                val exerciseName =
                                    workoutDays[i].exercises[j].sets[k].exercise
                                val exerciseCategory =
                                    workoutDays[i].exercises[j].sets[k].category
                                val Weight = workoutDays[i].exercises[j].sets[k].weight
                                val Reps = workoutDays[i].exercises[j].sets[k].reps

//                            System.out.println(Date + ", " + exerciseName+ ", " + exerciseCategory + ", " + Weight + ", " + Reps + "," + exerciseComment);
                                fos?.write("$Date,$exerciseName,$exerciseCategory,$Weight,$Reps,$exerciseComment\n".toByteArray())
                            }
                        }

                        // Deprecated
//                    for(int j = 0; j < workoutDays.get(i).getSets().size(); j++)
//                    {
//                        fos.write((workoutDays.get(i).getSets().get(j).getDate() + "," + workoutDays.get(i).getSets().get(j).getExercise() + "," + workoutDays.get(i).getSets().get(j).getCategory() + "," + workoutDays.get(i).getSets().get(j).getWeight() + "," + workoutDays.get(i).getSets().get(j).getReps() + "\n").getBytes());
//                    }
                    }
                    fos?.close()
                    toastMaker.makeText(
                        "File Written in " + dir,
                    )

                } catch (e: IOException) {
                    println(e.message)
                }
//            } else {
//                toastMaker.makeText(
//                    "External Storage Not Readable",
//                )
//            }


    }

    class CreateDocumentLauncherWrapper(val launcher : ActivityResultLauncher<String>) {
        var resultInvoke: ((Uri) -> Unit)? = null
    }


}
