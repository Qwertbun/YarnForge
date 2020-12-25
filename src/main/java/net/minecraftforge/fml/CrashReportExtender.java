/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

import cpw.mods.modlauncher.log.TransformingThrowablePatternConverter;
import joptsimple.internal.Strings;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.Logger;

public class CrashReportExtender {
	private static final List<ICrashCallable> crashCallables = Collections.synchronizedList(new ArrayList<>());

	public static void enhanceCrashReport(final CrashReport crashReport, final CrashReportSection category) {
		for (final ICrashCallable call : crashCallables) {
			category.add(call.getLabel(), call);
		}
	}

	public static void registerCrashCallable(ICrashCallable callable) {
		crashCallables.add(callable);
	}

	public static void registerCrashCallable(String headerName, Callable<String> reportGenerator) {
		registerCrashCallable(new ICrashCallable() {
			@Override
			public String getLabel() {
				return headerName;
			}

			@Override
			public String call() throws Exception {
				return reportGenerator.call();
			}
		});
	}

	public static void addCrashReportHeader(StringBuilder stringbuilder, CrashReport crashReport) {
	}

	public static String generateEnhancedStackTrace(final Throwable throwable) {
		return generateEnhancedStackTrace(throwable, true);
	}

	public static String generateEnhancedStackTrace(final StackTraceElement[] stacktrace) {
		final Throwable t = new Throwable();
		t.setStackTrace(stacktrace);
		return generateEnhancedStackTrace(t, false);
	}

	public static String generateEnhancedStackTrace(final Throwable throwable, boolean header) {
		final String s = TransformingThrowablePatternConverter.generateEnhancedStackTrace(throwable);
		return header ? s : s.substring(s.indexOf(Strings.LINE_SEPARATOR));
	}

	public static File dumpModLoadingCrashReport(final Logger logger, final LoadingFailedException error, final File topLevelDir) {
		final CrashReport crashReport = CrashReport.create(new Exception("Mod Loading has failed"), "Mod loading error has occurred");
		error.getErrors().forEach(mle -> {
			final Optional<IModInfo> modInfo = Optional.ofNullable(mle.getModInfo());
			final CrashReportSection category = crashReport.addElement(modInfo.map(iModInfo -> "MOD " + iModInfo.getModId()).orElse("NO MOD INFO AVAILABLE"));
			Throwable cause = mle.getCause();
			int depth = 0;
			while (cause != null && cause.getCause() != null && cause.getCause() != cause) {
				category.add("Caused by " + (depth++), cause + generateEnhancedStackTrace(cause.getStackTrace()).replaceAll(Strings.LINE_SEPARATOR + "\t", "\n\t\t"));
				cause = cause.getCause();
			}
			if (cause != null) {
				category.applyStackTrace(cause);
			}
			category.add("Mod File", () -> modInfo.map(IModInfo::getOwningFile).map(t -> ((ModFileInfo) t).getFile().getFileName()).orElse("NO FILE INFO"));
			category.add("Failure message", () -> mle.getCleanMessage().replace("\n", "\n\t\t"));
			category.add("Mod Version", () -> modInfo.map(IModInfo::getVersion).map(Object::toString).orElse("NO MOD INFO AVAILABLE"));
			category.add("Mod Issue URL", () -> modInfo.map(IModInfo::getOwningFile).map(ModFileInfo.class::cast).flatMap(mfi -> mfi.<String>getConfigElement("issueTrackerURL")).orElse("NOT PROVIDED"));
			category.add("Exception message", Objects.toString(cause, "MISSING EXCEPTION MESSAGE"));
		});
		final File file1 = new File(topLevelDir, "crash-reports");
		final File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-fml.txt");
		if (crashReport.writeToFile(file2)) {
			logger.fatal("Crash report saved to {}", file2);
		} else {
			logger.fatal("Failed to save crash report");
		}
		System.out.print(crashReport.asString());
		return file2;
	}
}
