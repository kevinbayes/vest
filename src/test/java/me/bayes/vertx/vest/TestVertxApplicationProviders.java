package me.bayes.vertx.vest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.bayes.vertx.vest.sample.BaseSampleProvider;
import me.bayes.vertx.vest.sample.LateSampleProvider;
import me.bayes.vertx.vest.sample.OtherSampleProvider;
import me.bayes.vertx.vest.sample.SampleProvider;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TestVertxApplicationProviders {

	@Test
	public void testGivenPackageToScanOneClassFound() {
		// Given
		VestApplication application = new VestApplication() {
		};
		Set<String> packages = new HashSet<String>(1);
		packages.add("me.bayes.vertx.vest.sample");

		// When
		application.addPackagesToScan(packages);

		// Then
		List<SampleProvider> providers = application.getProviders(SampleProvider.class);
		Assertions.assertThat(providers).hasSize(3);
		Assertions.assertThat(providers.get(0)).isExactlyInstanceOf(BaseSampleProvider.class);
		Assertions.assertThat(providers.get(1)).isExactlyInstanceOf(OtherSampleProvider.class);
		Assertions.assertThat(providers.get(2)).isExactlyInstanceOf(LateSampleProvider.class);
	}

}
