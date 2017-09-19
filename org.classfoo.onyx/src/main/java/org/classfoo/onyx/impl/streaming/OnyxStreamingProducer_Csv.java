package org.classfoo.onyx.impl.streaming;

import java.io.IOException;
import java.io.Reader;

import au.com.bytecode.opencsv.CSVReader;

public class OnyxStreamingProducer_Csv {

	public void readLine(Reader reader) throws IOException {
		CSVReader csvReader = new CSVReader(reader);
		String[] line =null;
		while((line =  csvReader.readNext())!=null){
			
		}
	}
}
