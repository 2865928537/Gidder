package net.antoniy.gidder.ui.fragment;

public final class FragmentFactory {
	public static BaseFragment createFragment(FragmentType fragmentType) {
		switch (fragmentType) {
		case USERS:
			return new UsersFragment();
		case REPOSITORIES:
			return new RepositoriesFragment();
		case SETTINGS:
			return new SettingsFragment();
		}
		
		return null;
	}
}
