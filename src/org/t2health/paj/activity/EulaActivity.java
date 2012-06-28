package org.t2health.paj.activity;

import t2.paj.R;

import org.t2health.paj.classes.*;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Displays an EULA screen
 * 
 */
public class EulaActivity extends ABSWebViewActivity 
{
	Button btnAccept;
	Button btnDeny;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.onEvent("EULAScreen-Open");
		
		btnAccept = (Button) this.findViewById(R.id.leftButton);
		btnAccept.setText(getString(R.string.eula_accept));
		btnAccept.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				AcceptPressed();
			}
		});

		btnDeny = (Button) this.findViewById(R.id.rightButton);
		btnDeny.setText("Decline");
		btnDeny.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				DeclinePressed();
			}
		});
		
		this.setTitle(getString(R.string.eula_title));
		this.setContent(getString(R.string.eula_content));
	}

	public void AcceptPressed() 
	{
		this.onEvent("EULA-Accepted");
		SharedPref.setIsEulaAccepted(true);
		if (SharedPref.getCoached()) 
		{
			this.startActivity(ActivityFactory.getCreateActivity(this));
		}
		else
		{
			this.startActivity(ActivityFactory.getWalkthroughActivity(this));
		}
		
		this.finish();
	}

	public void DeclinePressed() 
	{
		this.onEvent("EULA-Declined");
		this.setResult(0);
		this.finish();
	}

}
