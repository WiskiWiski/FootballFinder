package online.findfootball.android.user.auth.signup.tab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import online.findfootball.android.R;
import online.findfootball.android.user.auth.AuthUserObj;

/**
 * Created by Timur on 09.07.2017.
 */

public class SUPasswordFragment extends BaseSUFragment {
    private EditText editText;
    private EditText repeatPasswordEditText;

    private static final int MINIMAL_PASSWORD_LENGTH = 6;

    public SUPasswordFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.su_fragment_password, container, false);
        editText = rootView.findViewById(R.id.edit_text_password);
        repeatPasswordEditText = rootView.findViewById(R.id.edit_text_repeat_password);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!repeatPasswordEditText.getText().toString().isEmpty()) {
                    SUPasswordFragment.super.getParent().onDataStateChange(verifyData(false));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        repeatPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!editText.getText().toString().isEmpty()) {
                    SUPasswordFragment.super.getParent().onDataStateChange(verifyData(false));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return rootView;

    }

    @Override
    public void saveResult(@NonNull Object o) {
        if (editText != null) {
            AuthUserObj authUserObj = (AuthUserObj) o;
            authUserObj.setPassword(editText.getText().toString());
        }
    }

    @Override
    public void updateView(@NonNull Object o) {
        if (editText != null) {
            AuthUserObj authUserObj = (AuthUserObj) o;
            editText.setText(authUserObj.getPassword());
        }
    }

    @Override
    public boolean verifyData(boolean notifyUser) {
        if (editText == null || repeatPasswordEditText == null) {
            return false;
        }
        String password = String.valueOf(editText.getText());
        String rPassword = String.valueOf(repeatPasswordEditText.getText());
        if (!password.equals(rPassword)) {
            if (notifyUser) {
                Toast.makeText(getContext(), getString(R.string.su_password_frg_password_not_match),
                        Toast.LENGTH_SHORT).show();
                vibrate();
            }
        } else if (password.isEmpty()) {
            if (notifyUser) {
                Toast.makeText(getContext(), getString(R.string.su_password_frg_empty_password),
                        Toast.LENGTH_SHORT).show();
                vibrate();
            }
        } else if (password.length() < MINIMAL_PASSWORD_LENGTH) {
            if (notifyUser) {
                Toast.makeText(getContext(), getString(R.string.su_password_frg_password_too_short),
                        Toast.LENGTH_SHORT).show();
                vibrate();
            }
        } else {
            return true;
        }
        return false;
    }
}
