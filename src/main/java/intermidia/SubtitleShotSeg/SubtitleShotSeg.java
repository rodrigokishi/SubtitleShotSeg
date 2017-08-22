package intermidia.SubtitleShotSeg;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.fredy.jsrt.api.SRT;
import org.fredy.jsrt.api.SRTInfo;
import org.fredy.jsrt.api.SRTReader;
import org.fredy.jsrt.api.SRTWriter;
import org.fredy.jsrt.editor.SRTEditor;

import TVSSUnits.Shot;
import TVSSUnits.ShotList;
import TVSSUtils.ShotReader;

public class SubtitleShotSeg 
{
    public static void main( String[] args ) throws Exception
    {
        SRTInfo subtitleList = SRTReader.read(new File(args[0]));
        ShotList shotList = ShotReader.readFromCSV(args[1]);
        float FPS = Integer.parseInt(args[2]);
        
        
        Iterator<Shot> shotIterator = shotList.getList().iterator();
        int shotIndex = 1;
        int writtenShotIndex = 0;
        ArrayList<String> shotSubtitles = new ArrayList<String>();
        
        
        Shot actualShot;     
        actualShot = shotIterator.next();
        
        for(SRT actualSubtitle: subtitleList)
        {
        	float actualSubtitleFrameIndex = convertToFrame(actualSubtitle.startTime, FPS); 

        	while(shotIterator.hasNext() && actualSubtitleFrameIndex >= actualShot.getEndBoundary())
        	{
        		actualShot = shotIterator.next();
        		if(writtenShotIndex < shotIndex)
        		{
        			shotSubtitles.add(shotIndex + " \"\"");
        		}
        		shotIndex++;
        	}
        	if(actualSubtitleFrameIndex < actualShot.getEndBoundary())
        	{           	
        		shotSubtitles.add(shotIndex + " \"" + clearHtml(actualSubtitle) + "\"");
        		//shotSubtitles.add(shotIndex + " " + actualSubtitle.toString());
        		if(writtenShotIndex < shotIndex)
        		{
        			writtenShotIndex = shotIndex;
        		}
        	}
        	
        }
        
        //Cast away last shot with empty text if its index already appeared on output
        if(shotIterator.hasNext() && writtenShotIndex == shotIndex)
        {
        	shotIndex++;
        	shotIterator.next();
        }
        
        //To exhaust final shots without subtitles
        while(shotIterator.hasNext())
        {
       		shotSubtitles.add(shotIndex++ + " \"\"");
        	shotIterator.next();
        }
        shotSubtitles.add(shotIndex + " \"\"");
    	
      
        FileWriter subtitleWriter = new FileWriter(args[3]);                
        for(String shotSubtitle : shotSubtitles)
        {        	
        	subtitleWriter.write(shotSubtitle + "\n");
        }
        subtitleWriter.close();
        
        //Write a srt file
        shotIndex = 1;
        if(args.length > 4)
        {
        	for(Shot shot : shotList.getList())
        	{
        		Date dummyDate = new Date(0);
        		@SuppressWarnings("deprecation")
				int timezoneOffset = dummyDate.getTimezoneOffset() * 60 * 1000;
        		
        		Date startTime = new Date((long)((shot.getEndBoundary() * 1000) / FPS) + timezoneOffset - 500);        	
        		Date endTime = new Date((long)((shot.getEndBoundary() * 1000) / FPS) + timezoneOffset + 500);        		        		
        		
        		int insertIndex = findIndex(startTime, subtitleList);
        		SRT shotEnd = new SRT(insertIndex, startTime, endTime, "End of Shot " + shotIndex++);
        		SRTEditor.insertSubtitle(subtitleList, shotEnd);        		
        	}
        	File srtOutput = new File(args[4]);        	
        	SRTWriter.write(srtOutput, subtitleList);
        }
    }
        
	private static float convertToFrame(Date time, float FPS)    
	{  	
		//Must subtract getTimezoneOffset because it adds 3 hours of brazilian timezone
		@SuppressWarnings("deprecation")
		float elapsedSecs = ((float)time.getTime() / (float)1000) - (time.getTimezoneOffset() * 60); 
    	return elapsedSecs * FPS;     	    	
    }
	
	private static String clearHtml(SRT subtitle)
	{
		String fullLine = "";
    	for(String text : subtitle.text)
    	{        		
    		if(fullLine.compareTo("") != 0)
    		{
    			fullLine += " " + text;
    		}
    		else
    		{
    			fullLine = text;
    		}
    	}
    	return fullLine.replaceAll("\\<.*?>",""); 
	}
	
	private static int findIndex(Date point, SRTInfo list)
	{
		int index = 0;
		while(index < list.size() && point.after(list.get(index).startTime))
		{
			index++;
		}
		return list.get(index).number;
	}
}
