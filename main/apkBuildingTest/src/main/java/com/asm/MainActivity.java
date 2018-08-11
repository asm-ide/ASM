package com.asm;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.asm.gongbj.*;


public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
	}
	
	public void onCh(View v){
		EditText ed = (EditText)findViewById(R.id.ed);
		String str = ed.getText().toString();
		EditText ch = (EditText)findViewById(R.id.target);
		EditText lo = (EditText)findViewById(R.id.log);
		
		if("".equals(ch.getText().toString())){
			try{
				errList err[] = new checkErr(MainActivity.this).checkErr(str);
				String o = "";
				for(errList e : err){
					if(e!=null)o = o + "\n \n errCode : " + e.ErrCode + "\n errLineCode : " + e.ErrLineCode + "\n explane : " + e.explane + "\n filePath : " + e.filePath + "\n line : " + String.valueOf(e.line) + "\n type : " + String.valueOf(e.type);
				}
				lo.setText(o);
				
			}catch(Exception e){
				lo.setText(e.toString());
			}
		}else{
			try{
				tempData temp = new tempData();
				temp.addData(ch.getText().toString(),"abab");
				errList err[] = new checkErr(MainActivity.this).checkErr(str,ch.getText().toString(),temp);
				String o = "";
				for(errList e : err){
					if(e!=null)o = o + "\n \n errCode : " + e.ErrCode + "\n errLineCode : " + e.ErrLineCode + "\n explane : " + e.explane + "\n filePath : " + e.filePath + "\n line : " + String.valueOf(e.line) + "\n type : " + String.valueOf(e.type);
				}
				lo.setText(o);
			}catch(Exception e){
				lo.setText(e.toString());
			}
		}
		
		
		
		
	}
	
	public void onC(View v){
		EditText ed = (EditText)findViewById(R.id.ed);
		String str = ed.getText().toString();
		apkBuilder ab = new apkBuilder(MainActivity.this);
		ab.prepare();
		String log = "";
		try{
			Result re = ab.buildApk(str,1);
			log = log + re.getLogOut();
			if(re.getResult()==Result.RESULT_SUCCESS){
				re = ab.buildApk(str,2);
				log = log + re.getLogOut();
				if(re.getResult()==Result.RESULT_SUCCESS){
					re=ab.buildApk(str,3);
					log=log+re.getLogOut();
					if(re.getResult()==Result.RESULT_SUCCESS){
						re=ab.buildApk(str,4);
						log=log+re.getLogOut();
						if(re.getResult()==Result.RESULT_SUCCESS){
							re=ab.buildApk(str,5);
							log=log+re.getLogOut();
							if(re.getResult()==Result.RESULT_SUCCESS){
								re=ab.buildApk(str,6);
								log=log+re.getLogOut();

							}
						}
					}
				}
			}
			
		}catch(buildException e){
			Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
			log=log+e.toString();
		}
		EditText e=(EditText)findViewById(R.id.log);
		e.setText(log);
		
		
	}
}
