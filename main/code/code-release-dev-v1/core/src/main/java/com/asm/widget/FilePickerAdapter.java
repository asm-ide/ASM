package com.asm.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class FilePickerAdapter extends RecyclerView.Adapter
{
	public static class ViewHolder extends RecyclerView.ViewHolder {
		public File file;
		
		
		public ViewHolder(View itemView) {
			super(itemView);
		}
	}
	
	
	private ArrayList<File> files;
	private FilePicker parent;
	
	
	public FilePickerAdapter(FilePicker parent, File dir) {
		this(parent, dir.getAbsolutePath(), dir.list());
	}
	
	public FilePickerAdapter(FilePicker parent, String parentPath, String[] child) {
		this.parent = parent;
		load(parentPath, child);
	}
	
	public void load(String parent, String[] child) {
		files = new ArrayList<File>();
		for(String path : child) {
			files.add(new File(parent, path));
		}
		notifyDataSetChanged();
	}
	
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filepicker_listitem, parent, false));
	}
	
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
	{
		if(holder instanceof ViewHolder) {
			ViewHolder h = (ViewHolder) holder;
			h.file = files.get(position);
			ImageView thumb = (ImageView) h.itemView.findViewById(R.id.filepicker_listitemThumb);
			TextView title = (TextView) h.itemView.findViewById(R.id.filepicker_listitemTitle);
			
		}
	}
	
	@Override
	public int getItemCount()
	{
		return 0;
	}
}
