package me.lauriichan.minecraft.minestom.util.logger.slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;

final class Helper {

    private Helper() {
        throw new UnsupportedOperationException();
    }

    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        ObjectArrayFIFOQueue<File> queue = new ObjectArrayFIFOQueue<>();
        queue.enqueue(file);
        File[] files;
        while (!queue.isEmpty()) {
            file = queue.dequeue();
            if (file.isFile()) {
                file.delete();
                continue;
            }
            files = file.listFiles();
            if (files == null || files.length == 0) {
                file.delete();
                continue;
            }
            for (File current : files) {
                queue.enqueue(current);
            }
            queue.enqueue(file);
        }
    }

    public static File createFolder(File folder) {
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return null;
            }
            return folder;
        } else if (!folder.isDirectory()) {
            if (folder.delete()) {
                if (!folder.mkdirs()) {
                    return null;
                }
                return folder;
            }
            return null;
        }
        return folder;
    }

    public static File createFile(File file) {
        if (file.getParent() != null && !file.getParent().trim().isEmpty()) {
            if (createFolder(file.getParentFile()) == null) {
                return null;
            }
        }

        if (!file.exists()) {
            if (!createFile0(file)) {
                return null;
            }
            return file;
        } else if (!file.isFile()) {
            if (file.delete()) {
                if (!createFile0(file)) {
                    return null;
                }
                return file;
            }
            return null;
        }
        return file;
    }

    private static boolean createFile0(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    public static File[] unzip(File zip, File directory, boolean deleteZipOnEnd) throws IOException {
        if (!zip.exists() || !zip.isFile()) {
            return null;
        }
        if (!directory.exists()) {
            if (createFolder(directory) == null) {
                return null;
            }
        } else if (!directory.isDirectory()) {
            return null;
        }
        byte[] buffer = new byte[2048];
        ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zip));
        ZipEntry entry = inputStream.getNextEntry();
        while (entry != null) {
            File file = new File(directory, entry.getName());
            FileOutputStream fileOutput = new FileOutputStream(file);
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, length);
            }
            fileOutput.close();
            entry = inputStream.getNextEntry();
        }
        inputStream.closeEntry();
        inputStream.close();
        if (deleteZipOnEnd) {
            zip.delete();
        }
        return directory.listFiles();
    }

    public static void zip(String zipName, File directory, File... toZip) throws IOException {
        if (!directory.exists()) {
            if (createFolder(directory) == null) {
                return;
            }
        } else if (!directory.isDirectory()) {
            return;
        }
        zip(new File(directory, zipName));
    }

    public static void zip(File zipFile, File... toZip) throws IOException {
        if (toZip == null || toZip.length == 0) {
            return;
        }
        if (createFile(zipFile) == null) {
            return;
        }

        FileOutputStream fileOutput = new FileOutputStream(zipFile);
        ZipOutputStream zipOutput = new ZipOutputStream(fileOutput);
        int failed = 0;
        for (int index = 0; index < toZip.length; index++) {
            File file = toZip[index];
            if (file == null) {
                failed += 1;
                continue;
            }
            FileInputStream fileInput = new FileInputStream(file);
            ZipEntry entry = new ZipEntry(file.getName());
            zipOutput.putNextEntry(entry);
            byte[] bytes = new byte[2048];
            int length;
            while ((length = fileInput.read(bytes)) >= 0) {
                zipOutput.write(bytes, 0, length);
            }
            fileInput.close();
        }
        zipOutput.close();
        fileOutput.close();
        if (failed == toZip.length) {
            zipFile.delete();
        }
    }

    public static String stackTraceToString(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        stackTraceToBuilder(throwable, builder, false);
        return builder.toString();
    }

    private static void stackTraceToBuilder(Throwable throwable, StringBuilder builder, boolean cause) {
        StringBuilder stack = new StringBuilder();

        if (cause) {
            stack.append('\n');
            stack.append("Caused by: ");
        }
        stack.append(throwable.getClass().getName());
        stack.append(": ");
        stack.append(throwable.getLocalizedMessage());
        builder.append(stack.toString());
        stack = new StringBuilder();

        StackTraceElement[] stackTrace = throwable.getStackTrace();

        for (StackTraceElement element : stackTrace) {

            String fileName = element.getFileName();
            stack.append("\n");
            stack.append('\t');
            stack.append("at ");

            stack.append(element.getClassName());
            stack.append('.');
            stack.append(element.getMethodName());
            stack.append('(');
            if (fileName == null) {
                stack.append("Unknown Source");
            } else {
                stack.append(fileName);
                stack.append(':');
                stack.append(Integer.toString(element.getLineNumber()));
            }
            stack.append(')');
            builder.append(stack.toString());
            stack = new StringBuilder();
        }

        Throwable caused = throwable.getCause();
        if (caused != null) {
            stackTraceToBuilder(caused, builder, true);
        }
    }

}