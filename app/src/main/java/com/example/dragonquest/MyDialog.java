package com.example.dragonquest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        TextView titleView = new TextView(getActivity());
        titleView.setText("本当によろしいですか？");
        titleView.setTextSize(24);
        titleView.setTextColor(Color.WHITE);
        titleView.setBackgroundColor(getResources().getColor(R.color.black));
        titleView.setPadding(20, 20, 20, 20);
        titleView.setGravity(Gravity.CENTER);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //TextView titleView1 = new TextView(getActivity());

        builder.setCustomTitle(titleView)
                .setMessage("")
                .setPositiveButton("いいえ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // このボタンを押した時の処理
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // このボタンを押した時の処理
                            //Intent intent = new Intent(String.valueOf(Chara.class));
                            //startActivity(intent);
                        }

                });
        return builder.create();
    }
    @Override
    public void onStart() {
        super.onStart();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.black));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(getResources().getColor(R.color.red));
        }

    }
}

