package intermidia.SubtitleShotSeg;

import java.io.File;

import com.googlecode.mp4parser.srt.SrtParser;

public class SubtitleShotSeg 
{
    public static void main( String[] args )
    {
    	SrtParser srtParser = new SrtParser();
    	//srtParser.parse(new );
    }
}


/*import org.fredy.jsrt.api.SRT;
import org.fredy.jsrt.api.SRTInfo;
import org.fredy.jsrt.api.SRTReader;

public class SubtitleShotSeg 
{
    public static void main( String[] args )
    {
        SRTInfo info = SRTReader.read(new File(args[0]));
        for(SRT sub : info )
        {
        	System.out.println(sub.text);
        }
    }
}
*/