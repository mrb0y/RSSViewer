package in.surajx.rssviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;

import in.surajx.rssviewer.xml.RSSFeed;


public class ListActivity extends Activity {
    RSSFeed feed;
    ListView lv;
    CustomListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        // Get feed form the file
        feed = (RSSFeed) getIntent().getExtras().get("feed");

        // Initialize the variables:
        lv = (ListView) findViewById(R.id.listView);
        lv.setVerticalFadingEdgeEnabled(true);

        // Set an Adapter to the ListView
        adapter = new CustomListAdapter(this);
        lv.setAdapter(adapter);

        // Set on item click listener to the ListView
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                Bundle postInfo = new Bundle();
                postInfo.putString("link", feed.getFeedList().get(arg2).getItemLink());
                Intent postviewIntent = new Intent(ListActivity.this, ViewPostActivity.class);
                postviewIntent.putExtras(postInfo);
                startActivity(postviewIntent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.imageLoader.clearCache();
        adapter.notifyDataSetChanged();
    }

    class CustomListAdapter extends BaseAdapter {

        public ImageLoader imageLoader;
        private LayoutInflater layoutInflater;

        public CustomListAdapter(ListActivity activity) {

            layoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = new ImageLoader(activity.getApplicationContext());
        }

        @Override
        public int getCount() {
            // Set the total list item count
            return feed.getItemCount();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {

            // Inflate the item layout and set the views
            View listItem = convertView;
            if (listItem == null) {
                listItem = layoutInflater.inflate(R.layout.list_item, null);
            }

            // Initialize the views in the layout
            ImageView iv = (ImageView) listItem.findViewById(R.id.thumb);
            TextView tvTitle = (TextView) listItem.findViewById(R.id.title);
            TextView tvDesc = (TextView) listItem.findViewById(R.id.description);

            // Set the views in the layout
            imageLoader.DisplayImage(feed.getFeedList().get(pos).getItemImage(), iv);
            tvTitle.setText("[" + feed.getFeedList().get(pos).getItemCategory() + "] " + feed.getFeedList().get(pos).getItemTitle());
            tvDesc.setText(feed.getFeedList().get(pos).getItemDescription());

            return listItem;
        }
    }
}
