package com.asm.ui.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class SectionsPagerAdapter extends FragmentPagerAdapter
{
	public static class Section
	{
		private SectionsPagerAdapter mParent;
		private String mTitle;
		private Fragment mFragment;
		
		
		private Section(SectionsPagerAdapter parent) {
			mParent = parent;
		}
		
		public int getIndex() {
			return mParent.mList.indexOf(this);
		}
		
		public String getTitle() {
			return mTitle;
		}
		
		public Section setTitle(String title) {
			mTitle = title;
			return this;
		}
		
		public Fragment getFragment() {
			return mFragment;
		}
		
		public Section setFragment(Fragment fragment) {
			mFragment = fragment;
			return this;
		}
	}
	
	private ArrayList<Section> mList = new ArrayList<>();
	
	
	public SectionsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return getSection(position).getFragment();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return getSection(position).getTitle();
	}
	
	public Section getSection(int index) {
		return mList.get(index);
	}
	
	public void setSection(int index, Section section) {
		mList.set(index, section);
		notifyDataSetChanged();
	}
	
	public void putSection(Section section) {
		mList.add(section);
		notifyDataSetChanged();
	}
	
	public void removeSection(int index) {
		mList.remove(index);
		notifyDataSetChanged();
	}
	
	public void removeSection(Section target) {
		mList.remove(target);
		notifyDataSetChanged();
	}
	
	public Section newSection() {
		return new Section(this);
	}
}
