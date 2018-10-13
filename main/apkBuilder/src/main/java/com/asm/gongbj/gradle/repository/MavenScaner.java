package com.asm.gongbj.gradle.repository;

import android.content.*;
import android.os.*;
import android.util.*;
import com.asm.gongbj.gradle.*;
import com.asm.gongbj.gradle.sync.*;
import com.asm.gongbj.tools.*;
import com.asm.gongbj.utils.*;
import java.io.*;
import java.net.*;
import java.util.*;
import okhttp3.*;
import org.apache.maven.artifact.repository.metadata.*;
import org.apache.maven.artifact.repository.metadata.io.xpp3.*;
import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.*;
import org.codehaus.plexus.util.*;
import com.asm.gongbj.gradle.aar.*;

public class MavenScaner
{

	private MavenProgressListener progL;
	private Syncer.ErrorListener errorL;
	public MavenScaner()
	{

	}
	public void setProgressListener(MavenProgressListener mpl)
	{
		progL = mpl;
	}

	public void setErrorListener(Syncer.ErrorListener errorL)
	{
		this.errorL = errorL;
	}

	public void downloadMaven(SyncData syncData, String text, Context con)
	{
		if (progL == null)
		{
			progL = new MavenProgressListener(){
				@Override
				public void onDownloadStart(String link)
				{
					//Log.i("MavenScanner", "onDownloadStart : \n link : " + link);
				}
				@Override
				public void onDownloadProgressChanged(String link, int progress)
				{
					//Log.i("MavenScanner", "onDownloadProgressChanged : \n link : " + link + "\n progress : " + String.valueOf(progress));
				}
				@Override
				public void onDownloadEnd()
				{
					//Log.i("MavenScanner", "onDownloadEnd");
				}
			};
		}
		if (errorL == null)
		{
			errorL = new Syncer.ErrorListener(){
				@Override
				public boolean onError(ProgressFail progressFail)
				{
					Log.e("MavenScanner", progressFail.getExplane());
					return true;
				}
			};
		}
		String data[] = text.split(":");
		if (data.length != 3)
		{
			errorL.onError(new ProgressFail("Wrong dependency name", null, "Sync"));
			return;
		}
		downloadMaven(syncData, data[0], data[1], data[2], con);
	}

	public void downloadMaven(SyncData syncData, String groupId, String artifactId, String version, Context con)
	{
		urls = new ArrayList<>();

		int i = NetWorkUtil.getConnectivityStatus(con);
		if (i == NetWorkUtil.TYPE_NOT_CONNECTED)
		{
			ProgressFail pf = new ProgressFail("No Network Connected", null, "Syncer");
			AnalysisData ad = new AnalysisData();
			AnalysisData.ErrData ed = new AnalysisData.ErrData();
			ed.comment = "No Network Connected";
			ed.type = AnalysisData.ErrData.WARNING;
			ad.errData.add(ed);
			pf.setAnalysisData(ad);
			errorL.onError(pf);
			return;
		}

		getDownloadLink(syncData, groupId, artifactId, version);

		if (urls.size() == 0)
		{
			ProgressFail pf = new ProgressFail("Cannot find Dependency", null, "Syncer");
			AnalysisData ad = new AnalysisData();

			AnalysisData.ErrData ed = new AnalysisData.ErrData();
			ed.comment = "Cannot find Dependency : " + "\"" + groupId + ":" + artifactId + ":" + version + "\"";
			ed.type = AnalysisData.ErrData.ERROR;

			ad.errData.add(ed);

			pf.setAnalysisData(ad);
			errorL.onError(pf);
			return;
		}

		//download all links
		f : for (String str : urls)
		{
			String p = Environment.getExternalStorageDirectory().toString() + "/.ASM/Maven/" +  str.substring(str.indexOf("/"), str.lastIndexOf("/") + 1);
			File save = new File(p + "/" + str.substring(str.lastIndexOf("/") + 1));	
			if (!save.getParentFile().exists())save.getParentFile().mkdirs();
			////////////////
			if (save.getName().endsWith(".aar"))
			{
				//Case : AAR
				//###########################
				if (!save.exists())
				{
					cleanFile(save);
					boolean suc = downloadFile(str, save);
					if (!suc)
					{
						continue f;
					}
				}
				//Handle Downloaded Aar File
				AarManager am = new AarManager();
				try
				{
					am.sync(save.getAbsolutePath(), syncData);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}else
			{
				//JAR
				//########################
				if (!save.exists())
				{
					cleanFile(save);
					boolean suc = downloadFile(str, save);
					if (!suc)continue f;
				}
				//Handle Downloaded Jar File
				syncData.addScanedJar(save.getAbsolutePath());
				
			}
			///////////////
		}

	}
	List<String> urls;
	private void getDownloadLink(SyncData syncData, String groupId, String artifactId, String version)
	{
		//try to find maven-metadata.xml
		List<String> strList = syncData.getTopLevelGradleInfo().allprojects.repositories.mavenUrls;
		for (String str : strList)
		{
			String dnUrl = "";
			if (str.trim().endsWith("/"))
			{
				dnUrl = str + groupId.replace(".", "/") + "/" + artifactId.replace(".", "/");
			}
			else
			{
				dnUrl  = str + "/" + groupId.replace(".", "/") + "/" + artifactId.replace(".", "/");
			}
			OkHttpClient client= new OkHttpClient();
			Metadata m = getMetadata(client, dnUrl + "/maven-metadata.xml");
			if (m != null)
			{
				String v = getProperVersion(m.getVersioning(), version);
				if (v == null)return;

				String case1 = dnUrl.toString() + "/" + v.toString() + "/" + artifactId.toString() + "-" + v + ".pom";
				{
					Model model = readMavenModel(case1.toString());
					String dnPath = getDownload(dnUrl, v, model);

					if (dnPath != null && !(contains(urls, dnPath)))
					{
						urls.add(dnPath);
						for (Dependency d : model.getDependencies())
						{
							if (d.getScope() == null || d.getScope().equals("compile"))
							{
								if (!(containsText(urls, "/" + d.getGroupId() + "/" + d.getArtifactId() + "/")))
								{
									String d_v = d.getVersion();
									if (d_v == null)
									{
										d_v = "+";
									}
									getDownloadLink(syncData, d.getGroupId(), d.getArtifactId(), d_v);

								}

							}
						}
					}
				}

				
			}
		}
	
	}

	private String getDownload(String dnUrl, String v, Model model)
	{
		if (model == null)return null;
		String packaging = dnUrl + "/" + v + "/" + model.getArtifactId() + "-" + v;

		if (model.getPackaging() == null || model.getPackaging().equals("jar"))
		{
			packaging += ".jar";
		}
		else if (model.getPackaging().equals("aar"))
		{
			packaging += ".aar";
		}
		return packaging;
	}
	private Metadata getMetadata(OkHttpClient client, String urlStr)
	{
		try
		{
			NetWorkUtil.enableNetWorkAtMainThread();
			URI url = new URI(urlStr);
			Request request = new Request.Builder()
				.url(HttpUrl.get(url))
				.build();
			try
			{
				Response response = client.newCall(request).execute();
				if (response.isSuccessful())
				{
					return new MetadataXpp3Reader().read(response.body().charStream());
				}

			}
			catch (Throwable e)
			{
				e.toString();
			}
		}
		catch (URISyntaxException e)
		{
			e.toString();
		}

		return null;
	}

	private Model readMavenModel(String pomUrl)
	{
		Model model = null;
		InputStream input = null;

		try
		{
			URL u = new URL(pomUrl);
			input = u.openStream();
			model = new MavenXpp3Reader().read(new InputStreamReader(input));

			return model;
		}
		catch (Throwable e)
		{
			return null;
		}
		finally
		{
			try
			{IOUtil.close(input);}
			catch (Throwable e)
			{}
		}

	}

	private String getProperVersion(Versioning versioning, String version)
	{
		if (!version.contains("+"))
		{
			return version;
		}
		else if (version.equals("+"))
		{
			return versioning.getLatest();
		}
		else
		{
			String str = version.substring(0, version.indexOf("+"));
			String max = "";
			for (String ver : versioning.getVersions())
			{
				if (ver.startsWith(str))
				{
					if (max.equals(""))
					{
						max = ver;
					}
					else
					{
						max = compare(max, ver);
					}
				}
			}

			if (max.equals(""))return null;
			return max;
		}
	}

	private String compare(String val1, String val2)
	{
		String c1[] = val1.split("\\.");
		String c2[] = val2.split("\\.");

		int min = Math.min(c1.length, c2.length);
		for (int i = 0; i < min; i++)
		{
			int value1 = 0, value2 = 0;
			try
			{
				value1 = Integer.parseInt(c1[i]);
			}
			catch (Exception e)
			{

			}
			try
			{
				value2 = Integer.parseInt(c2[i]);
			}
			catch (Exception e)
			{

			}
			if (value1 > value2)
			{
				return val1;
			}
			else if (value1 < value2)
			{
				return val2;
			}
			else if (value1 == value2)
			{
				//return val1;
			}
		}
		return val1;
	}
	private boolean contains(List<String> list, String target)
	{
		for (String str : list)
		{
			if (str.toString().equals(target.toString()))
			{
				return true;
			}
		}
		return false;
	}

	private boolean containsText(List<String> list, String text)
	{
		for (String str : list)
		{
			if (str.toString().contains(text))
			{
				return true;
			}
		}
		return false;
	}

	private List<String> addListAtList(List<String> list, List<String> addList)
	{
		for (String ob : addList)
		{
			list.add(ob.toString());
		}
		return list;
	}

	public static interface MavenProgressListener
	{
		public void onDownloadStart(String link);
		public void onDownloadProgressChanged(String link, int progress);
		public void onDownloadEnd();
	}




	private boolean downloadFile(String urlPath, File savePath)
	{
		int count;
		try
		{
			progL.onDownloadStart(urlPath);

			//while(savePath.length()==0){
			savePath.delete();
			URL url = new URL(urlPath);

			URLConnection conection = url.openConnection();
			conection.connect();

			// this will be useful so that you can show a tipical 0-100%
			// progress bar
			int lenghtOfFile = conection.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream(),
														8192);

			// Output stream
			OutputStream output = new FileOutputStream(savePath);

			byte data[] = new byte[1024];

			long total = 0;

			while ((count = input.read(data)) != -1)
			{
				total += count;
				// publishing the progress....
				// After this onProgressUpdate will be called
				progL.onDownloadProgressChanged(urlPath, (int) ((total * 100) / lenghtOfFile));

				// writing data to file
				output.write(data, 0, count);
			}

			// flushing output
			output.flush();

			// closing streams
			output.close();
			input.close();
			//}


			progL.onDownloadEnd();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.e("Error: ", e.getMessage());
			return false;
		}
	}


	private void cleanFile(File file)
	{
		final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
		file.renameTo(to);
		to.delete();
	}

}
