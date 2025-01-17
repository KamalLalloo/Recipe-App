package com.example.homepage20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {

    private List<User> users;
    private List<User> usersFull; // Full list for filtering
    private LayoutInflater inflater;
    private AddFriendsFragment addFriendsFragment;

    UserAdapter(Context context, List<User> users, AddFriendsFragment fragment) {
        this.inflater = LayoutInflater.from(context);
        this.users = users;
        this.usersFull = new ArrayList<>(users); // Copy of users for filtering
        this.addFriendsFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        holder.userName.setText(user.getUsername() + " - " + user.getName());
        holder.addButton.setOnClickListener(v -> {
            if (addFriendsFragment != null) {
                addFriendsFragment.addFriend(user.getUsername());
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        Button addButton;

        ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            addButton = itemView.findViewById(R.id.add_friend_button);
        }
    }

    public void updateUsers(List<User> newUsers) {
        this.users.clear();
        this.users.addAll(newUsers);
        this.usersFull.clear();
        this.usersFull.addAll(newUsers); // Update the full list for filtering
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(usersFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User item : usersFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users.clear();
            users.addAll((List<User>) results.values);
            notifyDataSetChanged();
        }
    };
}

