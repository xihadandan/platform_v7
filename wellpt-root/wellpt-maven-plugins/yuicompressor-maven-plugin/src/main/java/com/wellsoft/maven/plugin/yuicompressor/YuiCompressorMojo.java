package com.wellsoft.maven.plugin.yuicompressor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * Apply compression on JS and CSS (using YUI Compressor).
 */
@Mojo(name = "compress", defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class YuiCompressorMojo extends MojoSupport {

	/**
	 * Read the input file using "encoding".
	 */
	@Parameter(defaultValue = "${project.build.sourceEncoding}")
	private String encoding;

	/**
	 * The output filename suffix.
	 */
	@Parameter
	private String suffix;

	/**
	 * If no "suffix" must be add to output filename (maven's configuration manage empty suffix like default).
	 */
	@Parameter
	private boolean nosuffix;

	/**
	 * Insert line breaks in output after the specified column number.
	 */
	@Parameter(defaultValue = "-1")
	private int linebreakpos;

	/**
	 * [js only] No compression
	 */
	@Parameter
	private boolean nocompress;

	/**
	 * [js only] Minify only, do not obfuscate.
	 */
	@Parameter
	private boolean nomunge;

	/**
	 * [js only] Preserve unnecessary semicolons.
	 */
	@Parameter
	private boolean preserveAllSemiColons;

	/**
	 * [js only] disable all micro optimizations.
	 */
	@Parameter
	private boolean disableOptimizations;

	/**
	 * force the compression of every files,
	 * else if compressed file already exists and is younger than source file, nothing is done.
	 */
	@Parameter
	private boolean force;

	/**
	 * show statistics (compression ratio).
	 */
	@Parameter(defaultValue = "true")
	private boolean statistics;

	/**
	 * use the input file as output when the compressed file is larger than the original
	 */
	@Parameter(defaultValue = "true")
	private boolean useSmallestFile;

	private long inSizeTotal_;
	private long outSizeTotal_;

	@Override
	protected String[] getDefaultIncludes() throws Exception {
		return new String[] { "**/*.css", "**/*.js" };
	}

	@Override
	public void beforeProcess() throws Exception {
		if (nosuffix) {
			suffix = "";
		}
	}

	@Override
	protected void afterProcess() throws Exception {
		if (statistics && (inSizeTotal_ > 0)) {
			getLog().info(
					String.format("total input (%db) -> output (%db)[%d%%]", inSizeTotal_, outSizeTotal_,
							((outSizeTotal_ * 100) / inSizeTotal_)));
		}
	}

	@Override
	protected void processFile(SourceFile src) throws Exception {
		if (getLog().isDebugEnabled()) {
			getLog().debug("compress file :" + src.toFile() + " to " + src.toDestFile(suffix));
		}
		File inFile = src.toFile();
		File outFile = src.toDestFile(suffix);

		getLog().debug("only compress if input file is younger than existing output file");
		if (!force && outFile.exists() && (outFile.lastModified() > inFile.lastModified())) {
			if (getLog().isInfoEnabled()) {
				getLog().info(
						"nothing to do, " + outFile
								+ " is younger than original, use 'force' option or clean your target");
			}
			return;
		}

		InputStreamReader in = null;
		OutputStreamWriter out = null;
		File outFileTmp = new File(outFile.getAbsolutePath() + ".tmp");
		FileUtils.forceDelete(outFileTmp);
		try {
			in = new InputStreamReader(new FileInputStream(inFile), encoding);
			if (!outFile.getParentFile().exists() && !outFile.getParentFile().mkdirs()) {
				throw new MojoExecutionException("Cannot create resource output directory: " + outFile.getParentFile());
			}
			getLog().debug("use a temporary outputfile (in case in == out)");

			getLog().debug("start compression");
			out = new OutputStreamWriter(buildContext.newFileOutputStream(outFileTmp), encoding);
			if (nocompress) {
				getLog().info("No compression is enabled");
				IOUtil.copy(in, out);
			} else if (".js".equalsIgnoreCase(src.getExtension())) {
				JavaScriptCompressor compressor = new JavaScriptCompressor(in, jsErrorReporter_);
				compressor.compress(out, linebreakpos, !nomunge, jswarn, preserveAllSemiColons, disableOptimizations);
			} else if (".css".equalsIgnoreCase(src.getExtension())) {
				compressCss(in, out);
			}
			getLog().debug("end compression");
		} finally {
			IOUtil.close(in);
			IOUtil.close(out);
		}

		boolean outputIgnored = useSmallestFile && inFile.length() < outFile.length();
		if (outputIgnored) {
			FileUtils.forceDelete(outFileTmp);
			FileUtils.copyFile(inFile, outFile);
			getLog().debug("output greater than input, using original instead");
		} else {
			FileUtils.forceDelete(outFile);
			FileUtils.rename(outFileTmp, outFile);
			buildContext.refresh(outFile);
			buildContext.refresh(outFileTmp);
		}

		if (statistics) {
			inSizeTotal_ += inFile.length();
			outSizeTotal_ += outFile.length();

			String fileStatistics;
			if (outputIgnored) {
				fileStatistics = String.format(
						"%s (%db) -> %s (%db)[compressed output discarded (exceeded input size)]", inFile.getName(),
						inFile.length(), outFile.getName(), outFile.length());
			} else {
				fileStatistics = String.format("%s (%db) -> %s (%db)[%d%%]", inFile.getName(), inFile.length(),
						outFile.getName(), outFile.length(), ratioOfSize(inFile, outFile));
			}

			getLog().info(fileStatistics);
		}
	}

	private void compressCss(InputStreamReader in, OutputStreamWriter out) throws IOException {
		try {
			CssCompressor compressor = new CssCompressor(in);
			compressor.compress(out, linebreakpos);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"Unexpected characters found in CSS file. Ensure that the CSS file does not contain '$', and try again",
					e);
		}
	}

	protected long ratioOfSize(File file100, File fileX) throws Exception {
		long v100 = Math.max(file100.length(), 1);
		long vX = Math.max(fileX.length(), 1);
		return (vX * 100) / v100;
	}
}
