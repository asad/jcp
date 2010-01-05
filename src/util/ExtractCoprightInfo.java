import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Properties;



public class ExtractCoprightInfo {
    
    //args[0] is the cdk.lib directory
    public static void main(String[] args) throws Exception{
        String linesep = System.getProperty("line.separator");
        FileOutputStream fos = new FileOutputStream(new File("lib-licenses.txt"));
        fos.write(new String("JCP contains the following libraries. Please read this for comments on copyright etc."+linesep+linesep).getBytes());
        fos.write(new String("Chemistry Development Kit, master version as of "+new Date().toString()+" (http://cdk.sf.net)"+linesep).getBytes());
        fos.write(new String("Copyright 1997-2009 The CDK Development Team"+linesep).getBytes());
        fos.write(new String("License: LGPL v2 (http://www.gnu.org/licenses/old-licenses/gpl-2.0.html)"+linesep).getBytes());
        fos.write(new String("Download: https://sourceforge.net/projects/cdk/files/"+linesep).getBytes());
        fos.write(new String("Source available at: http://sourceforge.net/scm/?type=git&group_id=20024"+linesep+linesep).getBytes());
        File[] files = new File(args[0]).listFiles(new JarFileFilter());
        for(int i=0;i<files.length;i++){
            if(new File(files[i].getPath()+".meta").exists()){
                Properties metaprops = new Properties();
                metaprops.load(new FileInputStream(new File(files[i].getPath()+".meta")));
                fos.write(new String(metaprops.getProperty("Library")+" "+metaprops.getProperty("Version")+" ("+metaprops.getProperty("Homepage")+")"+linesep).getBytes());
                fos.write(new String("Copyright "+metaprops.getProperty("Copyright")+linesep).getBytes());
                fos.write(new String("License: "+metaprops.getProperty("License")+" ("+metaprops.getProperty("LicenseURL")+")"+linesep).getBytes());
                fos.write(new String("Download: "+metaprops.getProperty("Download")+linesep).getBytes());
                fos.write(new String("Source available at: "+metaprops.getProperty("SourceCode")+linesep).getBytes());
            }
            if(new File(files[i].getPath()+".extra").exists()){
                fos.write(new String("The author says:"+linesep).getBytes());
                FileInputStream in = new FileInputStream(new File(files[i].getPath()+".extra"));
                int len;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) > 0){
                  fos.write(buf, 0, len);
                }
            }
            fos.write(linesep.getBytes());
        }
        fos.close();
    }


}

class JarFileFilter implements FileFilter
{
  private final String[] okFileExtensions = 
    new String[] {"jar"};

  public boolean accept(File file)
  {
    for (String extension : okFileExtensions)
    {
      if (file.getName().toLowerCase().endsWith(extension))
      {
        return true;
      }
    }
    return false;
  }
}
