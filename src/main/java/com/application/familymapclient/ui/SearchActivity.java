package com.application.familymapclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.familymapclient.backend.DataCache;
import com.application.familymapclient.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.List;

import Models.Event;
import Models.Person;

import static com.application.familymapclient.R.drawable.map_marker_icon;

/**
 * Implementaion for the Search Activity and functionality
 */
public class SearchActivity extends AppCompatActivity {

    static int PERSON_VIEW_TYPE = 0;
    static int EVENT_VIEW_TYPE = 1;

    DataCache dataCache = DataCache.getDataCache();

    SearchView searchView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set the searchview and listeners for it
        searchView = (SearchView) findViewById(R.id.search_query_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setAdapters(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setAdapters(newText);
                return true;
            }
        });

        //Set the recycler view and linear layout manager.
        recyclerView = findViewById(R.id.search_recycler_view);
        LinearLayoutManager searchLinearLayoutManager = new LinearLayoutManager(SearchActivity.this);
        searchLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(searchLinearLayoutManager);
    }

    //Set the adapters for the recycler view.  Called on search click or text change
    private void setAdapters(String query) {
        List<Person> matchingPersons = dataCache.getSearchResultsPersons(query);
        List<Event> matchingEvents = dataCache.getSearchResultsEvents(query);

        SearchAdapter searchAdapter = new SearchAdapter(matchingPersons, matchingEvents);
        recyclerView.setAdapter(searchAdapter);
    }

    /**
     * SearchAdapter class for working with RecyclerView
     */
    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<Person> persons;
        private final List<Event> events;

        public SearchAdapter(List<Person> persons, List<Event> events) {
            this.persons = persons;
            this.events = events;
        }

        public int getItemViewType(int position) {
            return position < persons.size() ? PERSON_VIEW_TYPE : EVENT_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.recyclerview_item, parent, false);
            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < persons.size()) {
                holder.bind(persons.get(position));
            } else {
                holder.bind(events.get(position - persons.size()));
            }
        }

        @Override
        public int getItemCount() {
            return persons.size() + events.size();
        }
    }

    /**
     * Private inner class for working with RecyclerView and Adapters
     */
    private class SearchViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        ImageView icon;
        TextView textTop;
        TextView textBottom;

        int viewType;

        //set all views
        public SearchViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            linearLayout = itemView.findViewById(R.id.search_event_linear_layout);
            icon = itemView.findViewById(R.id.search_event_icon);
            textTop = itemView.findViewById(R.id.search_event_info);
            textBottom = itemView.findViewById(R.id.search_event_owner_name);
        }

        //Overloaded bind function for Person
        public void bind(Person person) {
            String name = person.getFirstName() + " " + person.getLastName();
            textTop.setText(name);
            if (person.getGender().equalsIgnoreCase("m")) {
                icon.setImageDrawable(new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male)
                        .colorRes(R.color.maleColor).sizeDp(40));
            } else {
                icon.setImageDrawable(new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female)
                        .colorRes(R.color.femaleColor).sizeDp(40));
            }

            //Set onClick listener that maps to person activity if clicked.
            linearLayout.setClickable(true);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPersonActivity(person);
                }
            });
        }

        //Overloaded bind function for event
        public void bind(Event event) {
            icon.setImageResource(map_marker_icon);
            String eventText = event.getEventType() + ": " + event.getCity() + ", " + event.getCountry()
                    + " (" + event.getYear() + ")";
            Person associatedPerson = dataCache.getPersonMap().get(event.getPersonID());
            String name = associatedPerson.getFirstName() + " " + associatedPerson.getLastName();
            textTop.setText(eventText);
            textBottom.setText(name);

            //Set onClick listener that maps to event activity if clicked.
            linearLayout.setClickable(true);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEventActivity(event);
                }
            });
        }

        private void startPersonActivity(Person person) {
            Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
            dataCache.setCurrentPerson(person);
            startActivity(intent);
        }

        private void startEventActivity(Event event) {
            Intent intent = new Intent(getApplicationContext(), EventActivity.class);
            dataCache.setCurrentEventAndPerson(event);
            startActivity(intent);
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public ImageView getIcon() {
            return icon;
        }

        public TextView getTextTop() {
            return textTop;
        }

        public TextView getTextBottom() {
            return textBottom;
        }
    }
}

