package org.itxtech.simpleframework;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

/**
 * SimpleFramework Project
 *
 * @author PeratX
 * @link https://itxtech.org
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
public class ConsoleActivity extends AppCompatActivity implements Handler.Callback {
    private static Handler logUpdateHandler = null;

    private ScrollView scrollView;
    private Button buttonSend = null;
    private TextView textViewLog = null;
    private EditText editCommand = null;

    public static float fontSize = 16.0f;
    public static SpannableStringBuilder currentLog = new SpannableStringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        logUpdateHandler = new Handler(this);

        textViewLog = (TextView) findViewById(R.id.label_log);
        editCommand = (EditText) findViewById(R.id.edit_command);
        scrollView = (ScrollView) findViewById(R.id.logScrollView);
        buttonSend = (Button) findViewById(R.id.button_send);

        scrollView.setBackgroundColor(Color.BLACK);

        textViewLog.setTextSize(fontSize);

        editCommand.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View p1, int keyCode, KeyEvent p3) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    sendCommand();
                    return true;
                }
                return false;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendCommand();
            }
        });
        postAppend(currentLog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.console, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                currentLog = new SpannableStringBuilder();
                textViewLog.setText("");
                break;
            case R.id.menu_copy:
                ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("test", currentLog));
                Toast.makeText(this, R.string.message_copied, Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        textViewLog.append((CharSequence) msg.obj);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        return true;
    }

    private void sendCommand() {
        log("> " + editCommand.getText());
        FrameworkExecutor.writeCommand(editCommand.getText().toString());
        editCommand.setText("");
    }

    public static boolean postAppend(CharSequence data) {
        if (logUpdateHandler != null) {
            Message msg = new Message();
            msg.obj = data;
            logUpdateHandler.sendMessage(msg);
            return true;
        }
        return false;
    }

    public static void log(String line) {
        line = "<font>" + line.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace(" ", "&nbsp;")
                .replace("\u001b[m", "</font>")
                .replace("\u001b[0m", "</font>")
                .replace("\u001b[1m", "</font><font style=\"font-weight:bold\">")
                .replace("\u001b[3m", "</font><font style=\"font-style:italic\">")
                .replace("\u001b[4m", "</font><font style=\"text-decoration:underline\">")
                .replace("\u001b[8m", "</font><font>")
                .replace("\u001b[9m", "</font><font style=\"text-decoration:line-through\">")
                .replace("\u001b[38;5;16m", "</font><font color=\"#000000\">")
                .replace("\u001b[38;5;19m", "</font><font color=\"#0000AA\">")
                .replace("\u001b[38;5;34m", "</font><font color=\"#00AA00\">")
                .replace("\u001b[38;5;37m", "</font><font color=\"#00AAAA\">")
                .replace("\u001b[38;5;124m", "</font><font color=\"#AA0000\">")
                .replace("\u001b[38;5;127m", "</font><font color=\"#AA00AA\">")
                .replace("\u001b[38;5;214m", "</font><font color=\"#FFAA00\">")
                .replace("\u001b[38;5;145m", "</font><font color=\"#AAAAAA\">")
                .replace("\u001b[38;5;59m", "</font><font color=\"#555555\">")
                .replace("\u001b[38;5;63m", "</font><font color=\"#5555FF\">")
                .replace("\u001b[38;5;83m", "</font><font color=\"#55FF55\">")
                .replace("\u001b[38;5;87m", "</font><font color=\"#55FFFF\">")
                .replace("\u001b[38;5;203m", "</font><font color=\"#FF5555\">")
                .replace("\u001b[38;5;207m", "</font><font color=\"#FF55FF\">")
                .replace("\u001b[38;5;227m", "</font><font color=\"#FFFF55\">")
                .replace("\u001b[38;5;231m", "</font><font color=\"#FFFFFF\">");
        line = line + "</font><br />";
        CharSequence result = Html.fromHtml(line);
        currentLog.append(result);
        postAppend(result);
    }
}
