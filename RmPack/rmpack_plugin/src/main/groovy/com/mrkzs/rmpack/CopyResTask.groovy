package com.mrkzs.rmpack

import com.android.annotations.NonNull
import com.android.manifmerger.ManifestMerger2
import com.android.manifmerger.MergingReport
import com.android.manifmerger.XmlDocument
import com.android.utils.ILogger
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.mrkzs.rmpack.ext.CommResExtension
import com.mrkzs.rmpack.ext.PluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

import java.nio.charset.StandardCharsets

/**
 * Created by KINCAI
 * <p>
 * Desc 打包aar项目，因gradle未提供
 * <p>
 * Date 2020-04-08 15:09
 */
class CopyResTask extends DefaultTask {
    final static def TAG = 'copy res'

    @TaskAction
    void output() {
        println "$TAG task start"
        def rootExt = project.extensions.findByName(RmPack.EXTENSION_NAME) as PluginExtension
        if (rootExt == null) {
            println "$TAG root ext null"
            return
        }
        def ext = rootExt.getProperty(RmPack.EXTENSION_COMM_RES) as CommResExtension
        if (ext == null) {
            println "$TAG ext null"
            return
        }
        def resRootDirStr = ext.resRoot
        if (resRootDirStr == null || resRootDirStr.length() == 0) {
            println "$TAG res root dir path null"
            return
        }

        def resRootDir = project.file(resRootDirStr)
        if (resRootDir != null && resRootDir.exists()) {
            println "$TAG root dir $resRootDir"
            println "$TAG module dir $project.projectDir"
            //拷贝文件
            copy(resRootDir, project.projectDir)
            //合并AndroidManifest
            def baseManifestFile = project.file('/src/main/AndroidManifest_base.xml')
            if (!baseManifestFile.exists()) {
                println "$TAG base manifest not exists"
                return
            }
            def originManifest = project.file('/src/main/AndroidManifest.xml')
            if (!originManifest.exists()) {
                println "$TAG origin manifest not exists"
                return
            }
            def copyManifest = project.file('/src/main/AndroidManifest_origin_copy.xml')
            if (!copyManifest.exists()) {
                copy(originManifest, copyManifest)
            }
            def targetManifest = originManifest

            mergeManifest(project, targetManifest, baseManifestFile, copyManifest)
            println "$TAG manifest merger finished after replace Z_APPLICATION_ID to \${applicationId}"
            replaceManifestAttr(targetManifest)
        }
    }

    final static def Z_APPLICATION_ID = 'Z_APPLICATION_ID'
    final static def APPLICATION_ID = '${applicationId}'

    static void replaceManifestAttr(File manifestFile) {
        if (!manifestFile.exists()) {
            println "$TAG replace manifest file not exists"
            return
        }
        def reader
        def bufferReader
        def writer
        def bufferWriter
        StringBuilder stringBuilder = new StringBuilder()
        try {
            reader = new InputStreamReader(new FileInputStream(manifestFile), StandardCharsets.UTF_8)
            bufferReader = new BufferedReader(reader)
            def line = ''
            while ((line = bufferReader.readLine()) != null) {
                if (line.length() > 0 && line.contains(Z_APPLICATION_ID)) {
                    def replace = line.replace(Z_APPLICATION_ID, APPLICATION_ID)
                    stringBuilder.append(replace).append('\r\n')
                } else {
                    stringBuilder.append(line).append('\r\n')
                }
            }
            def manifestStr = stringBuilder.toString()
            if (manifestStr != null && manifestStr.length() > 0) {
                writer = new OutputStreamWriter(new FileOutputStream(manifestFile), StandardCharsets.UTF_8)
                bufferWriter = new BufferedWriter(writer)
                bufferWriter.write(manifestStr)
            } else {
                println "$TAG replace manifest replace file content empty"
            }
        } catch (IOException e) {
            println "$TAG replace manifest file exception ${e.getMessage()}"
            e.printStackTrace()
        } finally {
            try {
                if (bufferWriter != null) {
                    bufferWriter.close();
                }
                if (writer != null) {
                    writer.close();
                }

                if (bufferReader != null) {
                    bufferReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    static void mergeManifest(Project project, File targetManifest, File libraryManifest, File originMaifestFile) {
        File reportFile = project.file('build/embedManifestReport.txt')
        File aaptManifest = targetManifest

        ILogger mLogger = new MiLogger()
        try {
            ManifestMerger2.Invoker manifestMergerInvoker = ManifestMerger2.newMerger(originMaifestFile, mLogger, ManifestMerger2.MergeType.APPLICATION)
            manifestMergerInvoker.addLibraryManifest(libraryManifest)
            manifestMergerInvoker.setMergeReportFile(reportFile);
            MergingReport mergingReport = manifestMergerInvoker.merge()
            println "$TAG manifest merging result:" + mergingReport.getResult();
            MergingReport.Result result = mergingReport.getResult();
            switch (result) {
                case MergingReport.Result.WARNING:
                    // fall through since these are just warnings.
                case MergingReport.Result.SUCCESS:
                    XmlDocument xmlDocument = mergingReport.getMergedXmlDocument(MergingReport.MergedManifestKind.MERGED);
                    try {
                        String annotatedDocument = mergingReport.getActions().blame(xmlDocument);
                        println "$TAG manifest success $annotatedDocument"
                    } catch (Exception e) {
                        println("$TAG manifest success exception cannot print resulting xml");
                    }
                    if (aaptManifest.exists()) {
                        save(xmlDocument, aaptManifest);
                        println "$TAG manifest merger finish ${xmlDocument.prettyPrint()}"
                        println "$TAG manifest save saved to $aaptManifest"
                    }
                    break;
                case MergingReport.Result.ERROR:
                    throw new RuntimeException(mergingReport.getReportString());
                default:
                    throw new RuntimeException("Unhandled result type : " + mergingReport.getResult());
            }
        } catch (RuntimeException e) {
            e.printStackTrace()
            throw new RuntimeException(e);
        }
    }

    static void save(XmlDocument xmlDocument, File out) {
        try {
            Files.write(xmlDocument.prettyPrint(), out, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class MiLogger implements ILogger {
        @Override
        void error(Throwable t, String msgFormat, Object... args) {
        }

        @Override
        void warning(@NonNull String msgFormat, Object... args) {
        }

        @Override
        void info(@NonNull String msgFormat, Object... args) {
        }

        @Override
        void verbose(@NonNull String msgFormat, Object... args) {
        }
    }

    void copy(File fromFile, File toFile) {
        if (fromFile.isFile()) {
            copyFile(fromFile, toFile);
        } else {
            if (!toFile.exists()) {
                toFile.mkdirs();
            }

            File[] files = fromFile.listFiles();
            for (File file : files) {
                copy(file, new File(toFile.getAbsolutePath(), file.getName()));
            }

        }
    }

    void copyFile(File oldFile, File newFile) {
        InputStream inStream = null;
        FileOutputStream fos = null;
        try {
            // int byteSum = 0;
            int byteRead = 0;

            if (!oldFile.exists()) {
                return;
            }
            inStream = new FileInputStream(oldFile);
            fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                // byteSum += byteRead;
                fos.write(buffer, 0, byteRead);
            }
            fos.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (inStream != null)
                    inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
