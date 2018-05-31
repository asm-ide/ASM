package com.asm.coreui.activity;

import android.support.v7.app.*;
import android.os.*;
import com.asm.text.*;
import android.widget.EditText;
import android.view.*;
import android.support.v7.widget.*;
import android.widget.LinearLayout;
import com.asm.troubleshoot.*;


public class MainActivity extends AppCompatActivity
{
	UndoManager um = new UndoManager();
	EditText e;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ASMExceptionHandler.init(this);
		LinearLayout l = new LinearLayout(this);
		l.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
		Toolbar t = new Toolbar(this);
		e = new EditText(this);
		e.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
		e.addTextChangedListener(um);
		l.addView(t);
		l.addView(e);
		l.setOrientation(LinearLayout.VERTICAL);
		setContentView(l);
		setSupportActionBar(t);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("undo");
		menu.add("redo");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getTitle().toString()) {
			case "undo":
				um.undo(e.getText());
				break;
				
			case "redo":
				um.redo(e.getText());
				break;
				
			default:
				return false;
		}
		return true;
	}
}
