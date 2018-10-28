package com.asm.blocktest;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.asm.block.BlockElementsFactory;
import com.asm.block.BlockView;
import com.asm.block.Theme;
import com.asm.block.elements.NumberElement;
import com.asm.block.elements.TextElement;
import android.widget.*;
import android.view.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.main);
		BlockView b1 = (BlockView)findViewById(R.id.block1);
		BlockView b2 = (BlockView)findViewById(R.id.block2);
		BlockView b3 = (BlockView)findViewById(R.id.block3);
		BlockView b4 = (BlockView)findViewById(R.id.block4);
		BlockView b5 = (BlockView)findViewById(R.id.block5);

		
        BlockElementsFactory blf;

        BlockView.initMargin(this);

		b1.setType(BlockView.Type.TYPE_DEFUALT);
		blf = new  BlockElementsFactory();
        blf.elements.add(new TextElement("Defualt Block", new Theme(getApplicationContext())));
        b1.setElements(blf.build(new Theme(getApplicationContext())));


		b2.setType(BlockView.Type.TYPE_END);
        blf = new  BlockElementsFactory();
        blf.elements.add(new TextElement("End Block", new Theme(getApplicationContext())));
        blf.elements.add(new NumberElement(new Theme(getApplicationContext())));
        b2.setElements(blf.build(new Theme(getApplicationContext())));
		b2.setColor(Color.parseColor("#d65cd6"));
		
		b3.setType(BlockView.Type.TYPE_NUMBER);
		blf = new  BlockElementsFactory();
        blf.elements.add(new TextElement("number : ",new Theme(this)));
		blf.elements.add(new NumberElement(new Theme(getApplicationContext())));
        blf.elements.add(new NumberElement(new Theme(getApplicationContext())));
		b3.setElements(blf.build(new Theme(getApplicationContext())));
		b3.setColor(Color.parseColor("#9966ff"));
			
		b4.setType(BlockView.Type.TYPE_BOOL);
        blf = new  BlockElementsFactory();
        blf.elements.add(new TextElement("Bool Block", new Theme(getApplicationContext())));
        b4.setElements(blf.build(new Theme(getApplicationContext())));
		b4.setColor(Color.parseColor("#ffd500"));
		
		b5.setType(BlockView.Type.TYPE_STRING);
        blf = new  BlockElementsFactory();
        blf.elements.add(new TextElement("String Block", new Theme(getApplicationContext())));
        b5.setElements(blf.build(new Theme(getApplicationContext())));
		b5.setColor(Color.parseColor("#ff6680"));
	}


}
