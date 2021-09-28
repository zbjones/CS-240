package com.application.familymapclient.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.application.familymapclient.backend.DataCache;
import com.application.familymapclient.R;
import com.application.familymapclient.backend.Settings;
import com.application.familymapclient.ui.PersonActivity;
import com.application.familymapclient.ui.SearchActivity;
import com.application.familymapclient.ui.SettingsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.Event;
import Models.Person;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ROSE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;


/**
 * Fragment that contains a Google Map supportMapFragment.  Contains logic for dropping markers, lines,
 * and moving to other activities within the application including settings, search, and person.
 */
public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLoadedCallback {

    boolean isMainActivity = true;

    private GoogleMap map;

    private TextView infoWindowTextTop;
    private TextView infoWindowTextBottom;
    private ImageView genderIcon;

    private final HashMap<Marker, Event> markerEventMap = new HashMap<>();
    private final ArrayList<Polyline> mapPolyLines = new ArrayList<>();
    private Map<String, String> mapMarkerColors;

    private final DataCache dataCache = DataCache.getDataCache();
    private Settings settings = Settings.getSettings();

    public MapFragment() {
    }

    public MapFragment(boolean isMainActivity) {
        this.isMainActivity = isMainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.supportMap);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setOnMapLoadedCallback(MapFragment.this);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                setMarkers();

                //If we are dealing with an Event Activity, then we need additional logic
                if (!isMainActivity) {
                    drawLines();
                    centerOnEvent();
                    setInfoWindowEvent();
                }
            }
        });

        FrameLayout infoWindow = (FrameLayout) view.findViewById(R.id.infoWindow);

        infoWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoClicked();
            }
        });

        infoWindowTextTop = (TextView) view.findViewById(R.id.infoWindowTextTop);
        infoWindowTextBottom = (TextView) view.findViewById(R.id.infoWindowTextBottom);
        genderIcon = (ImageView) view.findViewById(R.id.treeIcon);

        return view;
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (isMainActivity) {
            inflater.inflate(R.menu.fragment_map_menu, menu);

            menu.findItem(R.id.searchMenuItem).setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search)
                    .colorRes(R.color.toolbarIcon).actionBarSize());

            menu.findItem(R.id.settingsMenuItem).setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gears)
                    .colorRes(R.color.toolbarIcon).actionBarSize());

        } else {
            inflater.inflate(R.menu.fragment_map_menu_only_up_button, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.searchMenuItem) {
//            Toast.makeText(getContext(), "The search menu button was clicked.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.settingsMenuItem) {
//            Toast.makeText(getContext(), "The settings menu button was clicked.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void infoClicked() {
//        Toast.makeText(getContext(), "The info text window was clicked.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), PersonActivity.class);
        startActivity(intent);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        dataCache.setCurrentEventAndPerson(markerEventMap.get(marker));
        dataCache.setCurrentPerson(dataCache.getPersonByID(dataCache.getCurrentEvent().getPersonID()));

        //make the lines for selected event, life story, spouse lines, family tree lines
        drawLines();

        //update the info box
        marker.showInfoWindow();
        setInfoWindowEvent();

        //moves camear to center on marker
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null && markerEventMap != null) {
            clearGoogleMap();
            setMarkers();
            drawLines();
        }
    }

    private void setInfoWindowEvent() {
        Person currentPerson = dataCache.getPersonByID(dataCache.getCurrentEvent().getPersonID());
        String personName = currentPerson.getFirstName() + " " + currentPerson.getLastName();
        infoWindowTextTop.setText(personName);

        String eventInfo = dataCache.getCurrentEvent().getEventType().toUpperCase() + ": " + dataCache.getCurrentEvent().getCity() + ", "
                + dataCache.getCurrentEvent().getCountry() + " (" + dataCache.getCurrentEvent().getYear() + ")";
        infoWindowTextBottom.setText(eventInfo);

        if (currentPerson == null) {
            Toast.makeText(getContext(), "The Person ID was not found and will not be displayed.",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (currentPerson.getGender().equalsIgnoreCase("m")) {
                genderIcon.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_male)
                        .colorRes(R.color.maleColor).sizeDp(40));
            } else {
                genderIcon.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_female)
                        .colorRes(R.color.femaleColor).sizeDp(40));
            }
        }
    }


    private void setMarkers() {
        map.setOnMarkerClickListener(this);

        //get filtered events
        ArrayList<Event> allEvents = new ArrayList<>();
        for (Map.Entry<String, Event> eventPair : dataCache.getFilteredEventMap().entrySet()) {
            allEvents.add(eventPair.getValue());
        }

        this.mapMarkerColors = dataCache.getMapMarkerColors();

        //create a marker for each filtered event according to the proper color scheme
        for (Event event : allEvents) {
            double latitude = event.getLatitude();
            double longitude = event.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);

            float color = getMarkerColor(event);

            MarkerOptions markerOptions = new MarkerOptions().position(latLng).
                    icon(BitmapDescriptorFactory.defaultMarker(color)).snippet(event.getCity() +
                    ", " + event.getCountry());
            Marker marker = map.addMarker(markerOptions);
            marker.setVisible(true);
            markerEventMap.put(marker, event);
        }
    }

    //get the marker color from the markerColor map.
    private float getMarkerColor(Event event) {
        String eventType = event.getEventType();
        return determineColor(mapMarkerColors.get(eventType));
    }

    //map a string for a color to a pre-determined float
    private float determineColor(String color) {
        if (color.equals("Blue")) {
            return HUE_BLUE;
        }
        if (color.equals("Green")) {
            return HUE_GREEN;
        }
        if (color.equals("Orange")) {
            return HUE_ORANGE;
        }
        if (color.equals("Yellow")) {
            return HUE_YELLOW;
        }
        if (color.equals("Red")) {
            return HUE_RED;
        }
        if (color.equals("Azure")) {
            return HUE_AZURE;
        }
        if (color.equals("Violet")) {
            return HUE_VIOLET;
        }
        if (color.equals("Cyan")) {
            return HUE_CYAN;
        }

        //should not occur
        System.out.println("Error in getting event type");
        return HUE_ROSE;
    }

    //Clears all lines and markers from the map.
    private void clearGoogleMap() {
        for (Marker marker : markerEventMap.keySet()) {
            marker.remove();
        }
        markerEventMap.clear();
        removeAllPolylines();
    }

    //Line drawing parent method. All lines drawn will stored in mapPolylines and will be deleted/redrawn as needed.
    private void drawLines() {
        removeAllPolylines();
        settings = Settings.getSettings();

        if (settings.isLifeStoryLines()) {
            drawLifeStoryLines();
        }
        if (settings.isFamilyTreelines()) {
            drawFamilyTreeLines();
        }
        if (settings.isSpouseLines()) {
            drawSpouseLines();
        }
    }

    private void drawLifeStoryLines() {
        List<Event> personEvents = dataCache.getFilteredEventsForPerson(dataCache.getCurrentPerson().getPersonID());
        for (int i = 0; i < personEvents.size() - 1; i++) {
            Event eventOne = personEvents.get(i);
            Event eventTwo = personEvents.get(i + 1);
            Polyline polyline = makePolyLine(new LatLng(eventOne.getLatitude(), eventOne.getLongitude()),
                    new LatLng(eventTwo.getLatitude(), eventTwo.getLongitude()), settings.getStoryLineColor(), 18);
            mapPolyLines.add(polyline);
        }
    }

    //Parent method to draw FamilyTreeLines
    private void drawFamilyTreeLines() {
        familyTreeHelper(dataCache.getCurrentPerson(), dataCache.getCurrentEvent(), 36);
    }

    //Split familyTree line drawing into mother and father sides
    private void familyTreeHelper(Person person, Event event, int width) {
        if (person.getFatherID() != null) {
            Person father = dataCache.getPersonMap().get(person.getFatherID());
            drawFathersSideLines(father, event, width);
        }
        if (person.getMotherID() != null) {
            Person mother = dataCache.getPersonMap().get(person.getMotherID());
            drawMothersSideLines(mother, event, width);
        }
    }

    //recursive function to draw a polyline on the father's side
    private void drawFathersSideLines(Person father, Event event, int width) {
        List<Event> fatherEventList = dataCache.getFilteredEventsForPerson(father.getPersonID());
        if (!fatherEventList.isEmpty()) {
            Event fatherEvent = fatherEventList.get(0);
            Polyline polyline = makePolyLine(new LatLng(event.getLatitude(), event.getLongitude()),
                    new LatLng(fatherEvent.getLatitude(), fatherEvent.getLongitude()), settings.getFamilyTreeColor(), width / 2);
            mapPolyLines.add(polyline);
            familyTreeHelper(father, fatherEvent, width / 2);
        }
    }

    //recursive function to draw a polyline on the mother's side
    private void drawMothersSideLines(Person mother, Event event, int width) {
        List<Event> motherEventList = dataCache.getFilteredEventsForPerson(mother.getPersonID());
        if (!motherEventList.isEmpty()) {
            Event motherEvent = motherEventList.get(0);
            Polyline polyline = makePolyLine(new LatLng(event.getLatitude(), event.getLongitude()),
                    new LatLng(motherEvent.getLatitude(), motherEvent.getLongitude()), settings.getFamilyTreeColor(), width / 2);
            mapPolyLines.add(polyline);
            familyTreeHelper(mother, motherEvent, width / 2);
        }
    }

    private void drawSpouseLines() {
        if (dataCache.getCurrentPerson().getSpouseID() != null) {
            List<Event> spouseEventList = dataCache.getFilteredEventsForPerson(dataCache.getCurrentPerson().getSpouseID());
            if (!spouseEventList.isEmpty()) {
                Event spouseEvent = spouseEventList.get(0);
                Polyline polyline = makePolyLine(new LatLng(dataCache.getCurrentEvent().getLatitude(), dataCache.getCurrentEvent().getLongitude()),
                        new LatLng(spouseEvent.getLatitude(), spouseEvent.getLongitude()), settings.getSpouseLineColor(), 18);
                mapPolyLines.add(polyline);
            }
        }
    }

    //Deletes all polylines from the map and clears the list of lines.  Called by clearGoogleMap()
    private void removeAllPolylines() {
        for (Polyline line : mapPolyLines) {
            line.remove();
        }
        mapPolyLines.clear();
    }

    //helper function that eliminates the polyline options syntax from every line creation call
    private Polyline makePolyLine(LatLng latlngOne, LatLng latLngTwo, int color, int width) {
        return map.addPolyline(new PolylineOptions().add(latlngOne, latLngTwo).color(color).width(width));
    }

    private void centerOnEvent() {
        LatLng eventLatLng = new LatLng(dataCache.getCurrentEvent().getLatitude(),
                dataCache.getCurrentEvent().getLongitude());

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, 4));
    }

    public void isMainActivity(boolean value) {
        isMainActivity = value;
    }
}