package net.md_5.bungee.util;

public enum Version {
	VUnknown(0, "Unbekannt"), VCloudnet(1, "Invalid Protocol"), V1_7_0_1(3, "1.7.0-1"), V1_7_2_5(4, "1.7.2-5"),
	V1_7_6_10(5, "1.7.6-10"), V1_8_X(47, "1.8.X"), V1_9_0(107, "1.9.0"), V1_9_1(108, "1.9.1"), V1_9_2(109, "1.9.2"),
	V1_9_3(110, "1.9.3"), V1_9_4(110, "1.9.4"), V1_10_0(210, "1.10.0"), V1_10_1(210, "1.10.1"), V1_10_2(210, "1.10.2"),
	V1_11_0(315, "1.11.0"), V1_11_1(316, "1.11.1"), V1_11_2(316, "1.11.2"), V1_12_0(335, "1.12.0"),
	V1_12_1(338, "1.12.1"), V1_12_2(340, "1.12.2"), V1_13_0(398, "1.13.0"), V1_13_1(401, "1.13.1"),
	V1_13_2(404, "1.13.2"), V1_14_0(477, "1.14.0"), V1_14_1(480, "1.14.1"), V1_14_2(485, "1.14.2"),
	V1_14_3(490, "1.14.3"), V1_14_4(498, "1.14.4"), V1_15_0(573, "1.15.0"), V1_15_1(575, "1.15.1"),
	V1_15_2(578, "1.15.2"), V1_16_0(735, "1.16.0"), V1_16_1(736, "1.16.1");

	private int id = 0;
	private String tag;

	public String getTag() {
		return tag;
	}

	public int getId() {
		return id;
	}

	Version(int i, String tag) {
		this.id = i;
		this.tag = tag;
	}

	public static Version getVersion(int i) {
		for (Version v : Version.values()) {
			if (v.getId() == i) {
				return v;
			}
		}
		return VUnknown;
	}
}
