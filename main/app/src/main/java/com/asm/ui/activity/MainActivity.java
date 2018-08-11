package com.asm.ui.activity;

import com.asm.R;
import com.asm.ASM;
import com.asm.ui.base.BaseActivity;
import com.asm.ui.base.PlaceHolderFragment;
import com.asm.ui.base.SectionsPagerAdapter;

import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;

import android.os.Bundle;
import android.widget.ImageView;


public class MainActivity extends BaseActivity
{
	private SectionsPagerAdapter mAdapter;
	private ViewPager mViewPager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		initToolbar();
		
		// find views
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		ImageView thumb = (ImageView) findViewById(R.id.image);
		
		mAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		
		mAdapter.putSection(mAdapter.newSection().setTitle("Apple").setFragment(PlaceHolderFragment.newFragment(R.layout.testlayout)));
		mAdapter.putSection(mAdapter.newSection().setTitle("Banana").setFragment(PlaceHolderFragment.newFragment(R.layout.testlayout)));
		mAdapter.putSection(mAdapter.newSection().setTitle("Peach").setFragment(PlaceHolderFragment.newFragment(R.layout.testlayout)));
		
		mViewPager.setAdapter(mAdapter);
		
		tabLayout.setupWithViewPager(mViewPager);
	}
}
