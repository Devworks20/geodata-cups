package com.geodata.cups.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.geodata.cups.Activity.RegistrationActivity;
import com.geodata.cups.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationAccountInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationAccountInfoFragment extends Fragment
{
    private static final String TAG = RegistrationAccountInfoFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistrationAccountInfoFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationAccountInformationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationAccountInfoFragment newInstance(String param1, String param2)
    {
        RegistrationAccountInfoFragment fragment = new RegistrationAccountInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View mainView;

    MaterialEditText edt_username,edt_password, edt_confirm_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        mainView = inflater.inflate(R.layout.fragment_registration_account_info, container, false);

        initViews();

        return mainView;
    }

    private void initViews()
    {
        edt_username         = mainView.findViewById(R.id.edt_username);
        edt_password         = mainView.findViewById(R.id.edt_password);
        edt_confirm_password = mainView.findViewById(R.id.edt_confirm_password);
    }

    public boolean checkFields()
    {
        if(Objects.requireNonNull(edt_username.getText()).toString().isEmpty())
        {
            Toast.makeText(getActivity(), "Please enter your username", Toast.LENGTH_SHORT).show();

            edt_username.requestFocus();
        }
        else if(Objects.requireNonNull(edt_password.getText()).toString().isEmpty())
        {
            Toast.makeText(getActivity(), "Please enter your password", Toast.LENGTH_SHORT).show();

            edt_password.requestFocus();
        }
        else if(Objects.requireNonNull(edt_confirm_password.getText()).toString().isEmpty())
        {
            Toast.makeText(getActivity(), "Please re-enter your password", Toast.LENGTH_SHORT).show();

            edt_confirm_password.requestFocus();
        }
        else if(!edt_password.getText().toString().equals(edt_confirm_password.getText().toString()))
        {
            Toast.makeText(getActivity(), "Password did not match", Toast.LENGTH_SHORT).show();

            edt_confirm_password.requestFocus();
        }

        else
        {
            return  true;
        }
        return false;
    }

    public void setAccountInformation()
    {
        try
        {
            RegistrationActivity registrationActivity = (RegistrationActivity) getActivity();

            String Username, Password;

            Username  = Objects.requireNonNull(edt_username.getText()).toString();
            Password  = Objects.requireNonNull(edt_confirm_password.getText()).toString();

            Objects.requireNonNull(registrationActivity).setAccountInfo(Username, Password);
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}