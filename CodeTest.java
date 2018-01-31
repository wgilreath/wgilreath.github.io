package com.adhoc;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeTest {


	public CodeTest() {
	}

	public final static String[][] importCSVFile(final String fileName, String[] header) {

		String[][] result = new String[0][0];
		ArrayList<String[]> list = new ArrayList<String[]>();
		String line = "";

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			line = br.readLine();
			header[0] = line;
			String[] data = line.split(",");
			final int len = data.length;

			while ((line = br.readLine()) != null) {
				data = line.split(",");
				list.add(data);

			}// end while

			br.close();

			result = new String[list.size()][len];

			for (int x = 0; x < list.size(); x++) {
				String[] fields = list.get(x);
				System.arraycopy(fields, 0, result[x], 0, fields.length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}// end try

		System.out.printf("Import CSV File: %s has %d lines %d fields.%n",
				fileName, result.length, result[0].length);

		return result;
	}// end readCSVFile

	public final static void exportCSVFile(final String[][] data,
			final String head, final String fileName) {

		try (PrintWriter br = new PrintWriter(new FileWriter(fileName))) {
			String[] header = head.split(",");

			for (int x = 0; x < header.length - 1; x++) {
				br.print(header[x]);
				br.print(",");
			}

			br.print(header[header.length - 1]);
			br.println();

			for (int x = 0; x < data.length; x++) {
				for (int y = 0; y < data[x].length - 1; y++) {
					br.print(data[x][y]);
					br.print(",");
				}
				br.print(data[x][data[x].length - 1]);
				br.println();
			}// end for

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}// end try

		System.out.printf("Export CSV File: %s has %d lines %d fields.%n",
				fileName, data.length, data[0].length);

	}// end exportCSVFile

	public final boolean checkZipCode(final String zipCode) {
		String[] val = this.zipsMap.get(zipCode);

		String state = val[1];

		ArrayList<String[]> statePlans = this.plansMap.get(state);

		if (statePlans == null) {
			System.out
					.printf("    Problem! Zipcode/state has no plans. zipcode: %s state: %s %n",
							zipCode, state);

			return false;
		}//end if

		String[] plan = statePlans.get(0);
		String rateArea = plan[1] + " " + plan[4];

		for (int x = 0; x < statePlans.size(); x++) {
			plan = statePlans.get(x);
			String ra = plan[1] + " " + plan[4];
			if (!ra.equalsIgnoreCase(rateArea)) {
				System.out
						.printf("    Problem! Zipcode rate area is ambigious. zipcode: %s %n",
								zipCode);

				return false;
			}
		}// end for

		return true;

	}// end checkZipCode

	private final void initializeData() {
		System.out.printf("Initialize data from CSV files.%n%n");

		String[] header = new String[1];

		this.zips = importCSVFile("zips.csv", header);   // zipcode state
																// county_code
																// name
																// rate_area
		
		this.plans = importCSVFile("plans.csv", header); // plan_id state
																// metal_level
																// rate
																// rate_area
		
		this.slcsp = importCSVFile("slcsp.csv", header); // zipcode rate

		this.header = header[0];

		// initialize zipcode map
		for (int x = 0; x < zips.length; x++) {
			zipsMap.put(zips[x][0], zips[x]);
		}//end for

		// initialize plans map
		for (int x = 0; x < plans.length; x++) {
			String key = plans[x][1];
			if (plansMap.containsKey(key)) {
				ArrayList<String[]> val = plansMap.get(key);
				val.add(plans[x]);
				plansMap.put(key, val);
			} else {
				ArrayList<String[]> val = new ArrayList<String[]>();
				val.add(plans[x]);
				plansMap.put(key, val);
			}//end if

		}//end for

	}// end initializeData

	private final void processData() {
		
		System.out.printf("%nStart Processing data...%n%n");

		main_for: for (int x = 0; x < this.slcsp.length; x++) {
			String zipCode = this.slcsp[x][0];
			if (!this.checkZipCode(zipCode)) {
				this.slcsp[x][1] = " ";
				continue main_for;
			} else {

				String[] val = this.zipsMap.get(zipCode);

				String state = val[1];

				ArrayList<String[]> statePlans = plansMap.get(state);

				SortedArrayList<String> list = new SortedArrayList<String>();
				if (statePlans == null) {
					this.slcsp[x][1] = " ";
					System.out
							.printf("    Problem! Zipcode/state has no plans. zipcode: %s state: %s %n",
									zipCode, state);
				} else {
					for (int y = 0; y < statePlans.size(); y++) {

						String[] planData = statePlans.get(y);
						if (planData[2].equalsIgnoreCase("Silver")) {

							for (int z = 0; z < planData.length; z++) {
								if (!list.contains(planData[3]))
									list.add(planData[3]);

							}//end for
						}//end if
					}//end for

					this.slcsp[x][1] = list.get(1);

					System.out.printf("    Success! Zipcode:%s Rate:%s %n", slcsp[x][0],
							slcsp[x][1]);

				}//end if
				
			}// end if

		}//end if
		
		System.out.printf("%nClose Processing data...%n%n");

	}// end processData

	public final void finalizeData() {
		System.out.printf("Exporting results to slcps.csv%n");

		exportCSVFile(this.slcsp, this.header, "slcsp.csv");

	}// end finalizeData

	private String    header = null;
	private String[][] zips  = null;
	private String[][] plans = null;
	private String[][] slcsp = null;

	// HashMap<String.zipcode> => String[5][zipcode,state,county_code,name,rate_area]
	private HashMap<String, String[]> zipsMap = new HashMap<String, String[]>(); 
	
	// HashMap<String.state> => ArrayList<String[5][plan_id,state,metal_level,rate,rate_area]>
	private HashMap<String, ArrayList<String[]>> plansMap = new HashMap<String, ArrayList<String[]>>(); 

	public static void main(String[] args) {

		final CodeTest codeTest = new CodeTest();

		codeTest.initializeData();
		codeTest.processData();
		codeTest.finalizeData();

		System.exit(0);
		
	}// end main

}// end CodeTest
