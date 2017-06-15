package org.itxtech.simpleframework;

import android.os.Environment;

import java.io.*;

/**
 * @author PeratX
 */
public class FrameworkExecutor {

    public static File appDirectory = null;
    private static File dataDirectory = new File(Environment.getExternalStorageDirectory(), MainActivity.name);

    private static Process process = null;
    private static InputStreamReader stdout = null;
    private static OutputStreamWriter stdin = null;

    public static File getDataDirectory() {
        File dir = dataDirectory;
        dir.mkdirs();
        return dir;
    }

    public static void kill() {
        try {
            process.destroy();
        } catch (Exception ignored) {

        }
    }

    public static boolean isRunning() {
        if (process == null){
            return true;
        }
        try {
            process.exitValue();
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public static void runFramework() {
        File f = new File(getDataDirectory(), "tmp");
        if (!f.isDirectory() && f.exists()) {
            f.delete();
        }
        f.mkdirs();
        String file = (new File(getDataDirectory(), "/SimpleFramework.phar").exists() ?
                "/SimpleFramework.phar" : "/src/iTXTech/SimpleFramework/SimpleFramework.php");
        File ini = new File(getDataDirectory() + "/php.ini");
        if (!ini.exists()) {
            try {
                ini.createNewFile();
                FileOutputStream os = new FileOutputStream(ini);
                os.write("phar.readonly=0\nphar.require_hash=1\ndate.timezone=Asia/Shanghai\nshort_open_tag=0\nasp_tags=0\nopcache.enable=1\nopcache.enable_cli=1\nopcache.save_comments=1\nopcache.fast_shutdown=0\nopcache.max_accelerated_files=4096\nopcache.interned_strings_buffer=8\nopcache.memory_consumption=128\nopcache.optimization_level=0xffffffff"
                        .getBytes("UTF8"));
                os.close();
            } catch (Exception ignored) {

            }
        }
        String[] args = new String[]{
                    appDirectory + "/php",
                    "-c",
                    getDataDirectory() + "/php.ini",
                    getDataDirectory() + file,
                    "--enable-ansi"
            };

        ProcessBuilder builder = new ProcessBuilder(args);
        builder.redirectErrorStream(true);
        builder.directory(getDataDirectory());
        builder.environment().put("TMPDIR", getDataDirectory() + "/tmp");
        try {
            process = builder.start();
            stdout = new InputStreamReader(process.getInputStream(), "UTF-8");
            stdin = new OutputStreamWriter(process.getOutputStream(), "UTF-8");
            Thread tMonitor = new Thread() {
                public void run() {
                    BufferedReader br = new BufferedReader(stdout);
                    while (isRunning()) {
                        try {
                            int size = 0;
                            char[] buffer = new char[8192];
                            StringBuilder s = new StringBuilder();
                            while ((size = br.read(buffer, 0, buffer.length)) != -1) {
                                s.setLength(0);
                                for (int i = 0; i < size; i++) {
                                    char c = buffer[i];
                                    switch (c) {
                                        case '\r':
                                            continue;
                                        case '\n':
                                            String line = s.toString();
                                            if (!line.startsWith("\u001B]0;")) {
                                                ConsoleActivity.log(line);
                                            }
                                        case '\u0007':
                                            s.setLength(0);
                                            break;
                                        default:
                                            s.append(c);
                                            break;
                                    }
                                }
                            }
                        } catch (IOException ignored) {

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                br.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    ConsoleActivity.log("[" + MainActivity.name + "] Server was stopped.");
                    MainActivity.actionHandler.obtainMessage(MainActivity.ACTION_STOP_SERVICE).sendToTarget();
                }
            };
            tMonitor.start();
        } catch (Exception e) {
            ConsoleActivity.log("[" + MainActivity.name + "] Unable to start " + "PHP.");
            ConsoleActivity.log(e.toString());
            MainActivity.actionHandler.obtainMessage(MainActivity.ACTION_STOP_SERVICE).sendToTarget();
            kill();
        }
    }

    public static boolean writeCommand(String cmd) {
        try {
            stdin.write(cmd + "\r\n");
            stdin.flush();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
