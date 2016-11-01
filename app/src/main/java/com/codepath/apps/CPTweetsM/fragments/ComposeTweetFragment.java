package com.codepath.apps.CPTweetsM.fragments;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
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
import com.codepath.apps.CPTweetsM.databinding.FragmentComposeTweetBinding;
import com.codepath.apps.CPTweetsM.models.Tweet;
import com.codepath.apps.CPTweetsM.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by chmanish on 10/28/16.
 */
public class ComposeTweetFragment extends DialogFragment {

    private FragmentComposeTweetBinding binding;
    private TwitterClient client;
    private EditText etTweet;
    private Button btnTweet;
    private ImageButton btnClose;
    private TextView tvChar;
    private Button btnDismiss;
    private Tweet newTweet;
    private static Tweet replyTweet;
    private String in_reply_to_status_id;
    private int mCharLeft = 140;

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
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_compose_tweet, container, false);
        View view = binding.getRoot();
        return view;
        //return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    private void showAlertDialog() {
        FragmentManager fm = getFragmentManager();
        SaveDraftFragment alertDialog = SaveDraftFragment.newInstance(etTweet.getText().toString());
        alertDialog.show(fm, "fragment_alert");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get fields from binding
        etTweet = binding.etTweet;
        tvChar = binding.tvChar;
        btnTweet = binding.btnTweet;
        btnClose = binding.btnClose;

        if(replyTweet != null){
            btnTweet.setText("Reply");
            String reply = replyTweet.getUser().getScreenName() + " ";
            etTweet.setText(reply);
            etTweet.setSelection((etTweet.getText().length()));
            etTweet.setCursorVisible(true);
            in_reply_to_status_id = Long.toString(replyTweet.getUid());
            mCharLeft -= (etTweet.getText().length());
            tvChar.setText(Integer.toString(mCharLeft));

        }

        SharedPreferences pref = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        String draft = pref.getString("draft", "");

        // Save draft only if its compose tweet
        if (draft != "" && replyTweet == null){
            etTweet.setText(draft + " ");
            etTweet.setSelection((etTweet.getText().length()));
            etTweet.setCursorVisible(true);
            mCharLeft -= (etTweet.getText().length());
            tvChar.setText(Integer.toString(mCharLeft));
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
                // Alert to save draft
                String draft = etTweet.getText().toString();
                if (replyTweet == null && !draft.equals(""))
                    showAlertDialog();
                dismiss();

            }
        });




        etTweet.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // this will show characters remaining
                int charLeft = mCharLeft - s.toString().length();
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
