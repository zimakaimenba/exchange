package com.exchangeinfomanager.bankuaifengxi.ai;

import java.time.LocalDate;

import com.github.cjwizard.WizardPage;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

public class BanKuaiWeeklyFengXi extends WizardPage
{

	public BanKuaiWeeklyFengXi(String title, String description, LocalDate selectdate) 
	{
		super (title,description);
		
		initializeGui ();
		
		
	}

	private void initializeGui() 
	{
JLabel label = new JLabel("\u70ED\u70B9\u677F\u5757");
		
		JLabel label_1 = new JLabel("\u70ED\u70B9\u4E2A\u80A1");
		
		JLabel label_2 = new JLabel("\u5F31\u52BF\u677F\u5757");
		
		JLabel label_3 = new JLabel("\u5F31\u52BF\u4E2A\u80A1");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(29)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(label_3)
						.addComponent(label_2)
						.addComponent(label_1)
						.addComponent(label))
					.addContainerGap(940, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(35)
					.addComponent(label)
					.addGap(18)
					.addComponent(label_1)
					.addGap(18)
					.addComponent(label_2)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(label_3)
					.addContainerGap(530, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
		
	}
}
