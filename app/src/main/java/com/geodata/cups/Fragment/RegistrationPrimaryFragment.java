package com.geodata.cups.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.geodata.cups.Backend.Retrofit.Model.Registration.DesignationModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.PoliceDistrictModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.PolicePrecintModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.PoliceStationModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.RankModel;
import com.geodata.cups.Backend.Retrofit.Model.Registration.SuffixModel;
import com.geodata.cups.Backend.Retrofit.Host.APIClient;
import com.geodata.cups.Backend.Retrofit.Interface.APIClientInterface;
import com.geodata.cups.Backend.Tools.VolleyCatch;
import com.geodata.cups.R;
import com.geodata.cups.Activity.RegistrationActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationPrimaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationPrimaryFragment extends Fragment
{

    private static final String TAG = RegistrationPrimaryFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistrationPrimaryFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationPrimaryFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static RegistrationPrimaryFragment newInstance(String param1, String param2)
    {
        RegistrationPrimaryFragment fragment = new RegistrationPrimaryFragment();

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

    MaterialEditText edt_rank, edt_firstName, edt_middleName,
                     edt_lastName, edt_suffix, edt_emailAddress, edt_mobileNumber,
                     edt_designation, edt_policeDistrict, edt_policeStation, edt_policeCommunityPrecinct;

    View mainView;

    AlertDialog alertDialog;

    VolleyCatch volleyCatch = new VolleyCatch();
    APIClientInterface apiInterface;

    ArrayList<Integer> RankID = new ArrayList<>();
    ArrayList<String> RankName = new ArrayList<>();

    ArrayList<Integer> DesignationID = new ArrayList<>();
    ArrayList<String> DesignationName = new ArrayList<>();

    ArrayList<Integer> SuffixID = new ArrayList<>();
    ArrayList<String> SuffixName = new ArrayList<>();

    ArrayList<Integer> PoliceDistrictID = new ArrayList<>();
    ArrayList<String> PoliceDistrictName = new ArrayList<>();

    ArrayList<Integer> PoliceStationID = new ArrayList<>();
    ArrayList<String> PoliceStationName = new ArrayList<>();

    ArrayList<Integer> PolicePrecintID = new ArrayList<>();
    ArrayList<String> PolicePrecintName = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mainView =  inflater.inflate(R.layout.fragment_registration_primary, container, false);

        initViews();

        return mainView;
    }

    private void initViews()
    {
        apiInterface = APIClient.getClient().create(APIClientInterface.class);

        edt_rank           = mainView.findViewById(R.id.edt_rank);
        edt_firstName      = mainView.findViewById(R.id.edt_firstName);
        edt_middleName     = mainView.findViewById(R.id.edt_middleName);
        edt_lastName       = mainView.findViewById(R.id.edt_lastName);
        edt_suffix         = mainView.findViewById(R.id.edt_suffix);
        edt_emailAddress   = mainView.findViewById(R.id.edt_emailAddress);
        edt_mobileNumber   = mainView.findViewById(R.id.edt_mobileNumber);
        edt_designation    = mainView.findViewById(R.id.edt_designation);
        edt_policeDistrict = mainView.findViewById(R.id.edt_policeDistrict);
        edt_policeStation  = mainView.findViewById(R.id.edt_policeStation);
        edt_policeCommunityPrecinct = mainView.findViewById(R.id.edt_policeCommunityPrecinct);

        initListeners();

        initGetGeneralReferences();
    }

    private void initListeners()
    {

        edt_rank.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initRankDropdown();
            }
        });
        edt_rank.setFocusable(false);

        edt_suffix.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initSuffixDropdown();
            }
        });
        edt_suffix.setFocusable(false);

        edt_designation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initDesignationDropdown();
            }
        });
        edt_designation.setFocusable(false);

        edt_policeDistrict.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                initPoliceDistrictDropdown();
            }
        });
        edt_policeDistrict.setFocusable(false);

        edt_policeStation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!edt_policeDistrict.getText().toString().isEmpty())
                {
                    initPoliceStationList(edt_policeDistrict.getTag().toString());
                }
                else
                {
                    Toast.makeText(getActivity(), "Please select your police district", Toast.LENGTH_SHORT).show();

                    edt_policeDistrict.requestFocus();
                }
            }
        });
        edt_policeStation.setFocusable(false);


        edt_policeCommunityPrecinct.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!edt_policeStation.getText().toString().isEmpty())
                {
                    initPolicePrecinctList(edt_policeStation.getTag().toString());
                }
                else
                {
                    Toast.makeText(getActivity(), "Please select your police station", Toast.LENGTH_SHORT).show();

                    edt_policeStation.requestFocus();
                }
            }
        });
        edt_policeCommunityPrecinct.setFocusable(false);
    }

    private void initRankDropdown()
    {
        try
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Select Rank");
            LinearLayout linearLayout = new LinearLayout(getActivity());
            final ListView listView = new ListView(getActivity());
            linearLayout.addView(listView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, RankName);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    edt_rank.setText(listView.getItemAtPosition(i).toString().trim());
                    edt_rank.setTag(RankID.get(i));
                    alertDialog.dismiss();
                }
            });
            builder.setView(linearLayout);
            alertDialog = builder.create();
            alertDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initSuffixDropdown()
    {
        try
        {
            final String[] StringArrayExtensionName = getResources().getStringArray(R.array.suffix_type);

            final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Select Suffix");
            LinearLayout linearLayout = new LinearLayout(getActivity());
            final ListView listView = new ListView(getActivity());
            linearLayout.addView(listView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, SuffixName);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    edt_suffix.setText(listView.getItemAtPosition(i).toString().trim());
                    edt_suffix.setTag(i);
                    alertDialog.dismiss();
                }
            });
            builder.setView(linearLayout);
            alertDialog = builder.create();
            alertDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initDesignationDropdown()
    {
        try
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Select Designation");
            LinearLayout linearLayout = new LinearLayout(getActivity());
            final ListView listView = new ListView(getActivity());
            linearLayout.addView(listView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, DesignationName);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    edt_designation.setText(listView.getItemAtPosition(i).toString().trim());
                    edt_designation.setTag(DesignationID.get(i));
                    alertDialog.dismiss();
                }
            });
            builder.setView(linearLayout);
            alertDialog = builder.create();
            alertDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private void initPoliceDistrictDropdown()
    {
        try
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Select Police District");
            LinearLayout linearLayout = new LinearLayout(getActivity());
            final ListView listView = new ListView(getActivity());
            linearLayout.addView(listView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, PoliceDistrictName);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                {
                    edt_policeDistrict.setText(listView.getItemAtPosition(i).toString().trim());
                    edt_policeDistrict.setTag(PoliceDistrictID.get(i));
                    alertDialog.dismiss();
                }
            });
            builder.setView(linearLayout);
            alertDialog = builder.create();
            alertDialog.show();
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }

    }


    private void initGetGeneralReferences()
    {
        initGetRankList();

        initGetDesignationList();

        initGetSuffixList();

        initPoliceDistrictList();
    }

    private void initGetRankList()
    {
        if (!haveNetworkConnection(requireContext()))
        {
            // Handle no network connection scenario here if needed
            return;
        }

        apiInterface.GetRankList().enqueue(new Callback<List<RankModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<RankModel>> call, @NonNull Response<List<RankModel>> response)
            {
                if (!response.isSuccessful())
                {
                    logAndWriteToFile("Rank List Failed: " + convertingResponseError(response.errorBody()));
                    return;
                }

                if (response.body() == null)
                {
                    logAndWriteToFile("Rank List : Server Response Null");
                    return;
                }

                RankID.clear();
                RankName.clear();

                for (RankModel rank : response.body())
                {
                    RankID.add(rank.getRankID());
                    RankName.add(rank.getRankName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<RankModel>> call, @NonNull Throwable t)
            {
                logAndWriteToFile("Rank List Failure: " + t.getMessage());
            }
        });
    }

    private void logAndWriteToFile(String log)
    {
        Log.e(TAG, log);
        volleyCatch.writeToFile(log); // Assuming volleyCatch is a custom logger
    }


    private void initGetDesignationList()
    {
        if (!haveNetworkConnection(requireContext()))
        {
            // Handle no network connection scenario here if needed
            return;
        }

        apiInterface.GetDesignationList().enqueue(new Callback<List<DesignationModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<DesignationModel>> call, @NonNull Response<List<DesignationModel>> response)
            {
                if (!response.isSuccessful())
                {
                    logAndWriteToFile("Designation List Failed: " + convertingResponseError(response.errorBody()));
                    return;
                }

                if (response.body() == null)
                {
                    logAndWriteToFile("Designation List: Server Response Null");
                    return;
                }

                DesignationID.clear();
                DesignationName.clear();

                for (DesignationModel designation : response.body())
                {
                    DesignationID.add(designation.getDesignationID());
                    DesignationName.add(designation.getDesignationName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DesignationModel>> call, @NonNull Throwable t)
            {
                logAndWriteToFile("Designation List Failure: " + t.getMessage());
            }
        });
    }

    private void initGetSuffixList()
    {
        if (!haveNetworkConnection(requireContext()))
        {
            // Handle no network connection scenario here if needed
            return;
        }

        apiInterface.GetSuffixList().enqueue(new Callback<List<SuffixModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<SuffixModel>> call, @NonNull Response<List<SuffixModel>> response)
            {
                if (!response.isSuccessful())
                {
                    String errorLog = "Suffix List Failed: " + convertingResponseError(response.errorBody());
                    logAndWriteToFile(errorLog);
                    return;
                }

                List<SuffixModel> suffixModels = response.body();
                if (suffixModels == null)
                {
                    String nullResponseLog = "Suffix List: Server Response Null";
                    logAndWriteToFile(nullResponseLog);
                    return;
                }

                SuffixID.clear();
                SuffixName.clear();

                for (SuffixModel suffix : suffixModels)
                {
                    SuffixID.add(suffix.getSuffixID());
                    SuffixName.add(suffix.getSuffixName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SuffixModel>> call, @NonNull Throwable t)
            {
                String failureLog = "Suffix List Failure: " + t.getMessage();
                logAndWriteToFile(failureLog);
            }
        });
    }

    private void initPoliceDistrictList()
    {
        if (!haveNetworkConnection(requireContext()))
        {
            // Handle no network connection scenario here if needed
            return;
        }

        apiInterface.GetPoliceDistrictList().enqueue(new Callback<List<PoliceDistrictModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<PoliceDistrictModel>> call, @NonNull Response<List<PoliceDistrictModel>> response)
            {
                if (!response.isSuccessful())
                {
                    String errorLog = "Police District List Failed: " + convertingResponseError(response.errorBody());
                    logAndWriteToFile(errorLog);
                    return;
                }

                List<PoliceDistrictModel> policeDistrictModels = response.body();
                if (policeDistrictModels == null)
                {
                    String nullResponseLog = "Police District List: Server Response Null";
                    logAndWriteToFile(nullResponseLog);
                    return;
                }

                PoliceDistrictID.clear();
                PoliceDistrictName.clear();

                for (PoliceDistrictModel district : policeDistrictModels)
                {
                    PoliceDistrictID.add(district.getPoliceDistrictID());
                    PoliceDistrictName.add(district.getPoliceDistrictName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PoliceDistrictModel>> call, @NonNull Throwable t)
            {
                String failureLog = "Police District List Failure: " + t.getMessage();
                logAndWriteToFile(failureLog);
            }
        });
    }

    private void initPoliceStationList(String PoliceDistrictID)
    {
        if (!haveNetworkConnection(requireContext()))
        {
            // Handle no network connection scenario here if needed
            return;
        }

        apiInterface.GetPoliceStationList(PoliceDistrictID).enqueue(new Callback<List<PoliceStationModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<PoliceStationModel>> call, @NonNull Response<List<PoliceStationModel>> response)
            {
                if (!response.isSuccessful())
                {
                    String errorLog = "Police District List Failed: " + convertingResponseError(response.errorBody());
                    logAndWriteToFile(errorLog);
                    return;
                }

                List<PoliceStationModel> policeStationModels = response.body();
                if (policeStationModels == null)
                {
                    String nullResponseLog = "Police District List: Server Response Null";
                    logAndWriteToFile(nullResponseLog);
                    return;
                }

                PoliceStationID.clear();
                PoliceStationName.clear();

                for (PoliceStationModel station : policeStationModels)
                {
                    PoliceStationID.add(station.getPoliceStationID());
                    PoliceStationName.add(station.getPoliceStationName());
                }

                initPoliceStationDropdown(); // Assuming this method sets up the dropdown
            }

            @Override
            public void onFailure(@NonNull Call<List<PoliceStationModel>> call, @NonNull Throwable t)
            {
                String failureLog = "Police District List Failure: " + t.getMessage();
                logAndWriteToFile(failureLog);
            }
        });
    }

    private void initPoliceStationDropdown()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select Police Station");

        LinearLayout linearLayout = new LinearLayout(getActivity());
        final ListView listView = new ListView(getActivity());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, PoliceStationName);

        linearLayout.addView(listView);
        listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                edt_policeStation.setText(PoliceStationName.get(i));
                edt_policeStation.setTag(PoliceStationID.get(i));

                if (alertDialog != null && alertDialog.isShowing())
                {
                    alertDialog.dismiss();
                }
            }
        });

        builder.setView(linearLayout);
        alertDialog = builder.create();
        alertDialog.show();
    }



    private void initPolicePrecinctList(String PoliceStationID)
    {
        if (!haveNetworkConnection(requireContext()))
        {
            // Handle no network connection scenario here if needed
            return;
        }

        apiInterface.GetPolicePrecintList(PoliceStationID).enqueue(new Callback<List<PolicePrecintModel>>()
        {
            @Override
            public void onResponse(@NonNull Call<List<PolicePrecintModel>> call, @NonNull Response<List<PolicePrecintModel>> response)
            {
                if (!response.isSuccessful())
                {
                    String errorLog = "Police Precinct List Failed: " + convertingResponseError(response.errorBody());
                    logAndWriteToFile(errorLog);
                    return;
                }

                List<PolicePrecintModel> policePrecintModels = response.body();
                if (policePrecintModels == null)
                {
                    String nullResponseLog = "Police Precinct List: Server Response Null";
                    logAndWriteToFile(nullResponseLog);
                    return;
                }

                PolicePrecintID.clear();
                PolicePrecintName.clear();

                for (PolicePrecintModel precinct : policePrecintModels)
                {
                    PolicePrecintID.add(precinct.getPolicePrecintID());
                    PolicePrecintName.add(precinct.getPolicePrecintName());
                }

                initPolicePrecinctDropdown(); // Assuming this method sets up the dropdown
            }

            @Override
            public void onFailure(@NonNull Call<List<PolicePrecintModel>> call, @NonNull Throwable t)
            {
                String failureLog = "Police Precinct List Failure: " + t.getMessage();
                logAndWriteToFile(failureLog);
            }
        });
    }

    private void initPolicePrecinctDropdown()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Select Police Community Precinct");

        LinearLayout linearLayout = new LinearLayout(getActivity());
        final ListView listView = new ListView(getActivity());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, PolicePrecintName);

        linearLayout.addView(listView);
        listView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                edt_policeCommunityPrecinct.setText(PolicePrecintName.get(i));
                edt_policeCommunityPrecinct.setTag(PolicePrecintID.get(i));

                if (alertDialog != null && alertDialog.isShowing())
                {
                    alertDialog.dismiss();
                }
            }
        });

        builder.setView(linearLayout);
        alertDialog = builder.create();
        alertDialog.show();
    }


    private String convertingResponseError(ResponseBody responseBody)
    {
        StringBuilder sb = new StringBuilder();

        try
        {
            if (responseBody != null)
            {
                BufferedReader reader;

                reader = new BufferedReader(new InputStreamReader(responseBody.byteStream()));

                String line;
                try
                {
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                    }
                }
                catch (IOException e)
                {
                    Log.e(TAG, e.toString());
                }
            }
            else
            {
                sb.append("");
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());

            sb.append("");
        }
        return sb.toString();
    }

    //Network Validation
    private boolean haveNetworkConnection(Context context)
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null)
        {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                haveConnectedWifi = true;
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                haveConnectedMobile = true;
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    public boolean checkFields()
    {
        if(Objects.requireNonNull(edt_rank.getText()).toString().isEmpty())
        {
            showToastAndRequestFocus("Please select your rank", edt_rank);
        }
        else if(Objects.requireNonNull(edt_firstName.getText()).toString().isEmpty())
        {
            showToastAndRequestFocus("Please enter your first name", edt_firstName);
        }
        else if(Objects.requireNonNull(edt_lastName.getText()).toString().isEmpty())
        {
            showToastAndRequestFocus("Please enter your last name", edt_lastName);
        }
        else if(Objects.requireNonNull(edt_emailAddress.getText()).toString().isEmpty())
        {
            showToastAndRequestFocus("Please enter your email address", edt_emailAddress);
        }
        else if(Objects.requireNonNull(edt_mobileNumber.getText()).toString().isEmpty())
        {
            showToastAndRequestFocus("Please enter your mobile number", edt_mobileNumber);
        }
        else if(Objects.requireNonNull(edt_designation.getText()).toString().isEmpty())
        {
            showToastAndRequestFocus("Please select your designation", edt_designation);
        }
        else if(Objects.requireNonNull(edt_policeDistrict.getText()).toString().isEmpty())
        {
            showToastAndRequestFocus("Please select your police district", edt_policeDistrict);
        }
        else
        {
            return true;
        }
        return false;
    }

    private void showToastAndRequestFocus(String message, EditText editText)
    {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        editText.requestFocus();
    }


    public void setPrimaryInformation()
    {
        try
        {
            RegistrationActivity registrationActivity = (RegistrationActivity) getActivity();

            String RankID, Firstname, Middlename="", Lastname,
                   SuffixID = "2", Email, Mobileno, DesignationID,
                   PoliceDistrictID,  PoliceStationID ="0", PolicePrecintID="0";

            RankID            = edt_rank.getTag().toString();
            Firstname         = Objects.requireNonNull(edt_firstName.getText()).toString();

            if (!edt_middleName.getText().toString().isEmpty())
            {
                Middlename        = Objects.requireNonNull(edt_middleName.getText()).toString();
            }

            Lastname          = Objects.requireNonNull(edt_lastName.getText()).toString();

            if (!Objects.requireNonNull(edt_suffix.getText()).toString().isEmpty())
            {
                SuffixID = edt_suffix.getTag().toString();
            }

            Email             = Objects.requireNonNull(edt_emailAddress.getText()).toString();
            Mobileno          = Objects.requireNonNull(edt_mobileNumber.getText()).toString();
            DesignationID     =  edt_designation.getTag().toString();
            PoliceDistrictID  = edt_policeDistrict.getTag().toString();

            if (!Objects.requireNonNull(edt_policeStation.getText()).toString().isEmpty())
            {
                PoliceStationID = edt_policeStation.getTag().toString();
            }

            if (!Objects.requireNonNull(edt_policeCommunityPrecinct.getText()).toString().isEmpty())
            {
                PolicePrecintID = edt_policeCommunityPrecinct.getTag().toString();
            }

            Objects.requireNonNull(registrationActivity).setPrimaryInfo(
                    RankID, Firstname, Middlename, Lastname,
                    SuffixID, Email, Mobileno,  DesignationID,
                    PoliceDistrictID, PoliceStationID,  PolicePrecintID
            );
        }
        catch (Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }
}