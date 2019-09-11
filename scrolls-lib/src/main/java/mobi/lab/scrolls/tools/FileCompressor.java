package mobi.lab.scrolls.tools;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Helper to compress log files.
 */
public class FileCompressor {

    /**
     * Takes in a File and creates a new File with the same path and name and replaces the original extension with ".zip".
     * This method does not do the actual compression, it just creates a path for the destination file.
     * Use {@link #compressFiles(File[], File)} for the actual compression work.
     *
     * @param uncompressedFile original File
     * @return Compressed File candidate
     */
    @NonNull
    public static File createCompressedFileCandidateFromUncompressedFilePath(@NonNull File uncompressedFile) {
        return new File(uncompressedFile.getParentFile(), uncompressedFile.getName().replaceFirst("[.][^.]+$", "") + ".zip");
    }

    /**
     * Compress a N files into one compressed file.
     *
     * @param targetFiles    Target files to compress
     * @param compressedFile Compressed file. Will be overwritten if exists.
     * @return true if the operation was success
     */
    @WorkerThread
    public static boolean compressFiles(@NonNull final File[] targetFiles, @NonNull final File compressedFile) {
        try {
            final int bufferSize = 4096;
            BufferedInputStream origin;
            final FileOutputStream dest = new FileOutputStream(compressedFile);
            final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            final byte[] data = new byte[bufferSize];
            for (File file : targetFiles) {
                FileInputStream fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, bufferSize);

                final ZipEntry entry = new ZipEntry(file.getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, bufferSize)) != -1) {
                    out.write(data, 0, count);
                }
                LogHelper.closeStream(origin);
            }
            LogHelper.closeStream(out);
            return true;
        } catch (Exception e) {
            Log.w("FileCompressor", "compressFiles failed", e);
            return false;
        }
    }
}
