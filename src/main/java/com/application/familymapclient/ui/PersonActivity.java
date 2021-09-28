package com.application.familymapclient.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.application.familymapclient.R;
import com.application.familymapclient.backend.DataCache;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Models.Event;
import Models.Person;

/**
 * Handles implementation of the Person Activity
 */
public class PersonActivity extends AppCompatActivity {

    DataCache dataCache = DataCache.getDataCache();

    private Person currentPerson;

    private TextView firstName;
    private TextView lastName;
    private TextView gender;

    private TextView lineOne;
    private TextView lineTwo;
    private ImageView icon;

    private ExpandableListView expandableListView;

    private ExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        //Get toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Person Details");

        //set back button enabled
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        currentPerson = dataCache.getCurrentPerson();

        firstName = (TextView) findViewById(R.id.personFirstName);
        lastName = (TextView) findViewById(R.id.personLastName);
        gender = (TextView) findViewById(R.id.personGender);

        firstName.setText(currentPerson.getFirstName());
        lastName.setText(currentPerson.getLastName());

        if (currentPerson.getGender().equalsIgnoreCase("m")) {
            gender.setText("Male");
        } else {
            gender.setText("Female");
        }

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListPersons);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (groupPosition == 0) {
                    //Move to Event Activity
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    dataCache.setCurrentEventAndPerson((Event) listAdapter.getChild(groupPosition, childPosition));
                    startActivity(intent);
                } else {
                    //Move to Person Activity
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    dataCache.setCurrentPerson((Person) listAdapter.getChild(groupPosition, childPosition));
                    startActivity(intent);
                }
                return false;
            }
        });
        drawExpandableList();
    }


    private void drawExpandableList() {
        Map<String, Person> relatedPersonMap = dataCache.getRelativesOfPerson(currentPerson.getPersonID());
        List<Person> personList = new ArrayList<>();
        for (Map.Entry<String, Person> relatedPersonEntry : relatedPersonMap.entrySet()) {
            personList.add(relatedPersonEntry.getValue());
        }

        List<Event> eventList = dataCache.getFilteredEventsForPerson(currentPerson.getPersonID());

        List<String> headerList = new ArrayList<>();
        headerList.add("Life Events");
        headerList.add("Relatives");

        listAdapter = new PersonActivityListAdapter(this, headerList, eventList, personList, currentPerson);
        expandableListView.setAdapter(listAdapter);
    }

    //implement back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    /**
     * List Adapter for displaying the expandable list view.
     */
    public class PersonActivityListAdapter extends BaseExpandableListAdapter {
        List<String> listDataHeader;
        List<Event> eventsList;
        List<Person> personsList;
        Person adapaterCurrentPerson;
        Context context;

        PersonActivityListAdapter(Context context, List<String> listDataHeader, List<Event> eventsList,
                                  List<Person> personsList, Person person) {
            this.context = context;
            this.listDataHeader = listDataHeader;
            this.eventsList = eventsList;
            this.personsList = personsList;
            adapaterCurrentPerson = person;
        }

        @Override
        public int getGroupCount() {
            return listDataHeader.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition == 0) {
                return eventsList.size();
            } else {
                return personsList.size();
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (groupPosition == 0) {
                return eventsList;
            } else {
                return personsList;
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (groupPosition == 0) {
                return eventsList.get(childPosition);
            } else {
                return personsList.get(childPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String title = listDataHeader.get(groupPosition);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandable_list_event_header, null);
            }
            TextView header = convertView.findViewById(R.id.event_header);
            header.setText(title);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandable_list_event_item, null);
            }

            lineOne = convertView.findViewById(R.id.event_list_info);
            lineTwo = convertView.findViewById(R.id.event_list_person);
            icon = convertView.findViewById(R.id.list_item_icon);

            if (groupPosition == 0) {
                Event currentEvent = (Event) getChild(groupPosition, childPosition);
                setEvent(currentEvent, convertView);
            } else {
                Person person = (Person) getChild(groupPosition, childPosition);
                setPerson(person, convertView);
            }
            return convertView;
        }

        private void setEvent(Event currentEvent, View convertView) {
            icon.setImageDrawable(convertView.getResources().getDrawable(R.drawable.map_marker_icon));
            String eventInfo = currentEvent.getEventType() + ", " + currentEvent.getCity() + ", "
                    + currentEvent.getCountry() + " " + currentEvent.getYear();
            lineOne.setText(eventInfo);
            Person currentPerson = dataCache.getPersonMap().get(currentEvent.getPersonID());
            String personInfo = currentPerson.getFirstName() + " " + currentPerson.getLastName();
            lineTwo.setText(personInfo);
        }

        private void setPerson(Person person, View convertView) {
            if (person.getGender().equalsIgnoreCase("m")) {
                icon.setImageDrawable(new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male)
                        .colorRes(R.color.maleColor).sizeDp(40));
            } else {
                icon.setImageDrawable(new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female)
                        .colorRes(R.color.femaleColor).sizeDp(40));
            }
            String personInfo = person.getFirstName() + " " + person.getLastName();
            lineOne.setText(personInfo);
            lineTwo.setText(dataCache.getRelationship(adapaterCurrentPerson, person));
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}