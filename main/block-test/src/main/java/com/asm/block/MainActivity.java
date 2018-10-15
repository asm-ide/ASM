package com.asm.block;


import android.app.*;
import android.os.*;
import com.asm.block.elements.*;
import android.graphics.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		BlockView b1 = (BlockView)findViewById(R.id.block1);
		BlockView b2 = (BlockView)findViewById(R.id.block2);
		BlockView b3 = (BlockView)findViewById(R.id.block3);
		BlockView b4 = (BlockView)findViewById(R.id.block4);
		BlockView b5 = (BlockView)findViewById(R.id.block5);

		b1.setType(BlockView.Type.TYPE_DEFUALT);
		b1.setElements(new BlockElements(new Theme(getApplicationContext())){
				public int count()
				{
					return 1;
				}
				public BlockElement get(int index)
				{
					return new TextElement("Defualt Block", new Theme(getApplicationContext()));
				}
			});
			
		b2.setType(BlockView.Type.TYPE_END);
		b2.setElements(new BlockElements(new Theme(getApplicationContext())){
				public int count()
				{
					return 1;
				}
				public BlockElement get(int index)
				{
					return new TextElement("End Block", new Theme(getApplicationContext()));
				}
			});
		b2.setColor(Color.parseColor("#d65cd6"));
		
		b3.setType(BlockView.Type.TYPE_NUMBER);
		b3.setElements(new BlockElements(new Theme(getApplicationContext())){
				public int count()
				{
					return 1;
				}
				public BlockElement get(int index)
				{
					return new TextElement("Number Block", new Theme(getApplicationContext()));
				}
			});
		b3.setColor(Color.parseColor("#9966ff"));
			
		b4.setType(BlockView.Type.TYPE_BOOL);
		b4.setElements(new BlockElements(new Theme(getApplicationContext())){
				public int count()
				{
					return 1;
				}
				public BlockElement get(int index)
				{
					return new TextElement("Bool Block", new Theme(getApplicationContext()));
				}
			});
		b4.setColor(Color.parseColor("#ffd500"));
		
		b5.setType(BlockView.Type.TYPE_STRING);
		b5.setElements(new BlockElements(new Theme(getApplicationContext())){
				public int count()
				{
					return 1;
				}
				public BlockElement get(int index)
				{
					return new TextElement("String Block", new Theme(getApplicationContext()));
				}
			});
		b5.setColor(Color.parseColor("#ff6680"));
	}


}
