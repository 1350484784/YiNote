package com.cs.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cs.dao.NoteDao;
import com.cs.note.R;
import com.cs.util.TextFormat;

public class ShowNoteAdapter extends CursorAdapter{

	private Context mcontext;
    private Cursor mcursor;
    //private String userName;
    
    private String selection;
    private String[] selectionArgs;
    /*
	@SuppressWarnings("deprecation")
	public ShowNoteAdapter(Context context, Cursor cursor,String name) {
		super(context, cursor);
		this.mcontext = context;
        this.mcursor = cursor;
        this.userName = name;
	}
	*/
    @SuppressWarnings("deprecation")
	public ShowNoteAdapter(Context context,Cursor cursor, String selection,String[] selectionArgs){
    	super(context, cursor);
		this.mcontext = context;
		this.mcursor = cursor;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (!mcursor.moveToPosition(position)) {
            Toast.makeText(mcontext, "couldn't move cursor to position ", Toast.LENGTH_SHORT).show();
          throw new IllegalStateException("couldn't move cursor to position " + position);
		}
		View v;
        if (convertView == null) {
            v = newView(mcontext, mcursor, parent);
        } else {
            v = convertView;
        }
		bindView(v, mcontext, mcursor);
        return v;
	}

	@Override
	public int getCount() {
		if(mcursor != null){
			NoteDao mNoteDao = new NoteDao(mcontext);
			//mcursor = mNoteDao.queryNote("userName=?",new String[]{userName});
			mcursor = mNoteDao.queryNote(selection, selectionArgs);
			Log.i("count", mcursor.getCount()+"");
			return mcursor.getCount();
		}else {
			return 0;
		}
	}
	
	@Override
	public void changeCursor(Cursor cursor) {
		Cursor old = swapCursor(cursor);  
        if (old != null) {  
            old.close();  
        } 
	}

	
	@Override
	public Object getItem(int position) {
		if(mcursor != null){
			mcursor.moveToPosition(position);
			return mcursor;
		}else{
			return null;
		}
	}
	
	@Override
	public long getItemId(int position) {
		if (mcursor != null) {
            if (mcursor.moveToPosition(position)) {
                return mcursor.getColumnIndex("_id");
            } else {
                return 0;
            }
        } else {
            return 0;
        }
	}


	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mcontext);
		View view = inflater.inflate(R.layout.item_note, parent, false);
		
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mTvTitle = (TextView) view.findViewById(R.id.id_tv_note_title);
		viewHolder.mTvContent = (TextView) view.findViewById(R.id.id_tv_note_summary);
		viewHolder.mTvCreateTime = (TextView) view.findViewById(R.id.id_tv_note_create_time);
		view.setTag(viewHolder);
		
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		String title = cursor.getString(cursor.getColumnIndex("title"));
		viewHolder.mTvTitle.setText(title);
		viewHolder.mTvContent.setText(TextFormat.getNoteSummary(cursor.getString(cursor.getColumnIndex("content"))));
		viewHolder.mTvCreateTime.setText(cursor.getString(cursor.getColumnIndex("create_time")));
	}
	
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		// TODO Auto-generated method stub
		return super.swapCursor(newCursor);
	}


	final class ViewHolder {
        TextView mTvTitle;
        TextView mTvContent;
        TextView mTvCreateTime;
    }
}
