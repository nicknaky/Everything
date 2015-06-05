package com.mushroomrobot.finwiz.settings;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mushroomrobot.finwiz.R;

/**
 * Created by NLam.
 */
public class PinFragment extends Fragment {

    SharedPreferences sharedPreferences;

    EditText editPin1, editPin2;
    TextView pinError, pinTitle;

    int setPin, pinEntry1, pinEntry2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pin, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.edit().putInt(getActivity().getResources().getString(R.string.pref_pin_key), 0);

        editPin1 = (EditText) rootView.findViewById(R.id.pinEditBox1);
        editPin2 = (EditText) rootView.findViewById(R.id.pinEditBox2);
        pinError = (TextView) rootView.findViewById(R.id.pinMessage);
        pinTitle = (TextView) rootView.findViewById(R.id.pin_title_dialog);

        pinTitle.setText(getActivity().getResources().getString(R.string.create_pin_dialog));

        editPin1.addTextChangedListener(new PinWatcher1());
        editPin2.addTextChangedListener(new PinWatcher2());

        return rootView;
    }

    public class PinWatcher1 implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1){
                pinError.setVisibility(TextView.INVISIBLE);
            }

            if (s.length() == 4){
                pinEntry1 = Integer.valueOf(s.toString());
                editPin1.setVisibility(EditText.INVISIBLE);
                editPin2.setVisibility(EditText.VISIBLE);
                editPin1.setText("");
                pinTitle.setText(getActivity().getResources().getString(R.string.confirm_pin_dialog));

            }

        }
    }
    public class PinWatcher2 implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (s.length() == 4){
                pinEntry2 = Integer.valueOf(s.toString());

                if (pinEntry2 == pinEntry1){
                    setPin = pinEntry2;
                    sharedPreferences.edit().putInt(getActivity().getResources().getString(R.string.pref_pin_key), setPin).commit();

                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    getActivity().finish();
                    startActivity(intent);

                    Toast.makeText(getActivity(), "PIN successfully set.", Toast.LENGTH_SHORT).show();
                } else {
                    pinError.setVisibility(TextView.VISIBLE);
                    editPin2.setVisibility(EditText.INVISIBLE);
                    editPin1.setVisibility(EditText.VISIBLE);
                    editPin2.setText("");
                    pinTitle.setText(getActivity().getResources().getString(R.string.create_pin_dialog));
                }
            }

        }
    }
}
