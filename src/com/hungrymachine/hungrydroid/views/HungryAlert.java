package com.hungrymachine.hungrydroid.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.hungrymachine.hungrydroid.R;
import com.hungrymachine.hungrydroid.utils.HungryLogger;

/**
 * A simple alert with a single "OK" button to dismiss. Pass a Runnable into 'show' -- Android does not support blocking
 * modal dialogs. You should perform any subsequent actions in the Runnable's run method.
 *
 * @author davesims
 */
public class HungryAlert extends AlertDialog {
    private Runnable task;
    private final static String name = "HungryAlert";

    private HungryAlert(Context context, String title, Runnable task) {
        this(context, DialogType.OK, task);
        this.setTitle(title);
    }

    private HungryAlert(Context context, DialogType type, final Runnable task) {
        super(context);
        this.task = task;
        switch (type) {
            case OK:
                initOkButton(context);
                break;
            case OK_CANCEL:
                initOkCancelButton(context);
                break;
        }
    }

    public HungryAlert(Context context, Runnable task) {
        this(context, "", task);
    }

    private void initOkCancelButton(Context context) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        task.run();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No action
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    private void initOkButton(Context context) {
        this.setButton(context.getString(R.string.hd_ok), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                HungryAlert.this.task.run();
                try {
                    dialog.dismiss();
                } catch (Exception ex) {
                    HungryLogger.e(name, "Exception thrown closing HungryAlert, moving on...");
                }
            }
        });
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception ex) {
            HungryLogger.e(name, "Exception thrown attempting to show an HungryAlert for message: " + this.toString());
        }
    }

    /**
     * Show a simple alert dialog. Is non-blocking, so it uses a Runnable in to execute any subsequent actions
     * to be performed when dialog is dismissed.
     *
     * @param message
     * @param context
     * @param task
     */
    public static void show(String message, Context context, Runnable task) {
        HungryAlert alert = new HungryAlert(context, task);
        alert.setMessage(message);
        alert.show();
    }

    /**
     * Show a simple alert dialog.
     *
     * @param message
     * @param context
     */
    public static void show(String message, Context context) {
        HungryAlert alert = new HungryAlert(context, new Runnable() {
            public void run() {
            }
        });
        alert.setMessage(message);
        alert.show();
    }

    /**
     * Show a simple alert dialog with a title.
     *
     * @param message
     * @param context
     */
    public static void show(String message, String title, Context context) {
        HungryAlert alert = new HungryAlert(context, title, new Runnable() {
            public void run() {
            }
        });
        alert.setMessage(message);
        alert.show();
    }

    /**
     * Show an alert dialog with a message and OK/Cancel buttons..
     *
     * @param message
     * @param context
     */
    public static void showOkCancelDialog(String message, Context context, final Runnable runnable) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        runnable.run();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No action
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("OK", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .setMessage(message).show();
    }


    public enum DialogType {
        OK,
        OK_CANCEL
    }

}
