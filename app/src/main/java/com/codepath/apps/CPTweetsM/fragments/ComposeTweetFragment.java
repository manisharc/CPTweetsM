package com.codepath.apps.CPTweetsM.fragments;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.CPTweetsM.R;
import com.codepath.apps.CPTweetsM.TwitterApplication;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.codepath.apps.CPTweetsM.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by chmanish on 10/28/16.
 */
public class ComposeTweetFragment extends DialogFragment {

    private TwitterClient client;
    private EditText etTweet;
    private Button btnTweet;
    private ImageButton btnClose;
    private TextView tvChar;
    private Button btnDismiss;
    private Tweet newTweet;
    private static Tweet replyTweet;
    private String in_reply_to_status_id;

    public ComposeTweetFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeTweetFragment newInstance(Tweet reply) {
        ComposeTweetFragment frag = new ComposeTweetFragment();
        replyTweet = reply;
        return frag;
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface ComposeTweetDialogListener {
        void onFinishComposeDialog(Tweet newTweet);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        client = TwitterApplication.getRestClient(); //singleton client
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get fields from view
        etTweet = (EditText) view.findViewById(R.id.etTweet);
        tvChar = (TextView) view.findViewById(R.id.tvChar);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        btnClose = (ImageButton) view.findViewById(R.id.btnClose);

        if(replyTweet != null){
            btnTweet.setText("Reply");
            String reply = replyTweet.getUser().getScreenName() + " ";
            etTweet.setText(reply);
            etTweet.setSelection((etTweet.getText().length()));
            etTweet.setCursorVisible(true);
            in_reply_to_status_id = Long.toString(replyTweet.getUid());

        }

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return input text back to activity through the implemented listener
                client.postTweet(new JsonHttpResponseHandler(){
                    // Success

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // File the Tweet structure from the information
                        ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
                        newTweet = Tweet.fromJSON(response);
                        listener.onFinishComposeDialog(newTweet);
                        // Close the dialog and return back to the parent activity
                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        // What should happen on failure
                        Toast.makeText(getContext(), "Tweet failed to upload. Try again?", Toast.LENGTH_SHORT).show();
                    }
                }, etTweet.getText().toString(), in_reply_to_status_id);


            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        etTweet.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // this will show characters remaining
                int charLeft = 140 - s.toString().length();
                tvChar.setText(Integer.toString(charLeft));
                if (charLeft < 0) {
                    tvChar.setTextColor(Color.RED);
                    btnTweet.setEnabled(false);
                }
                else {
                    btnTweet.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }


    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.75), (int) (size.y * 0.5));
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

}
