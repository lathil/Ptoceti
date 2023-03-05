package com.ptoceti.osgi.deviceaccess.impl;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.device.DriverSelector;
import org.osgi.service.device.Match;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Default device matching strategy that find a driver from a provided list of devices
 */

public class DefaultMatchingStrategy implements MatchingStrategy {

    // the provided list a of drivers
    ServiceReference[] driversSRef;

    DriverSelector driverSelector;

    /**
     * @param existingDriversSRef ServiceReference[] a list of existing drivers service referenecs
     */
    public DefaultMatchingStrategy(ServiceReference[] existingDriversSRef, DriverSelector selector) {
        driversSRef = existingDriversSRef;
        driverSelector = selector;
    }

    /**
     * Match device with existing drivers
     *
     * @param deviceSRef a ref to the device service
     * @return a match indicating the driver matched and attached.
     */
    public Match doMatch(ServiceReference deviceSRef) {
        return doMatchDrivers(deviceSRef, driversSRef);
    }

    /**
     * Find a matching driver for a given device.
     *
     * @param deviceSRef the device service reference
     * @return
     */
    public Match doMatchDrivers(ServiceReference deviceSRef, ServiceReference[] driversReferences) {
        Match result = null;

        List<Match> matchesList = new ArrayList<Match>();

        if (driversReferences != null) {
            for (ServiceReference driverSRef : driversReferences) {
                Driver driver = (Driver) Activator.bc.getService(driverSRef);
                try {
                    int match = driver.match(deviceSRef);
                    if (match > Device.MATCH_NONE) {
                        matchesList.add(new MatchResult(driverSRef, match));
                    }
                } catch (Exception e) {
                    Activator.getLogger().debug("Error matching device with driver: " + Utils.driverDetails(driverSRef));
                }

            }
        }

        result = doAttachBestMatch(deviceSRef, matchesList);
        return result;
    }

    protected Match doAttachBestMatch(ServiceReference deviceSRef, List<Match> matchesList) {

        Match bestMatch = null;


        if (driverSelector != null) {
            // if we get a driver selector, we use it to get the best match
            try {
                int bestmatchindex = DriverSelector.SELECT_NONE;
                bestmatchindex = driverSelector.select(deviceSRef, matchesList.toArray(new Match[matchesList.size()]));
                if (bestmatchindex != DriverSelector.SELECT_NONE) {
                    bestMatch = matchesList.get(bestmatchindex);
                }
            } catch (Exception ex) {
                // catch possible selection
                Activator.getLogger().debug("Exception while select match with DirverSelector: " + ex.toString());
            }
        }

        if (bestMatch == null) {
            // if no best match from selector use default protocol
            bestMatch = doGetBestMatch(matchesList);
        }
        if (bestMatch == null) {
            // no match
            doNoDriverFound(deviceSRef);
        } else {
            // got a match, do attach
            try {
                String attachResult = doAttach(deviceSRef, bestMatch);
                if (attachResult == null) {
                    // attach successful, return the match
                    return bestMatch;
                } else {
                    // return result is a referal to a diver id, find from existing drivers
                    ServiceReference driverServiceReferal = null;
                    for (Match match : matchesList) {
                        String driverID = null;
                        driverID = Utils.getDriverID(match.getDriver());
                        // find driver service reference from driverID
                        if (driverID.equals(attachResult)) {
                            driverServiceReferal = match.getDriver();
                            break;
                        }
                    }
                    if (driverServiceReferal != null) {
                        // found driver service reference, re-do matching + attach
                        return doMatchDrivers(deviceSRef, new ServiceReference[]{driverServiceReferal});
                    }
                }
            } catch (Exception ex) {
                Activator.getLogger().debug("Exception while attempting to attach driver: " + Utils.driverDetails(bestMatch.getDriver()));
                // exception with this match, try again but without it.
                List<Match> shortenMatchList = new ArrayList<Match>(matchesList);
                shortenMatchList.remove(bestMatch);
                return doAttachBestMatch(deviceSRef, shortenMatchList);
            }
        }

        return bestMatch;
    }

    /**
     * Try to attaching the driver to the device with the matched result
     *
     * @param deviceSRef the device the driver must attach to
     * @param match      the match result containing the driver servicereference
     * @return null if matching was successful, or driver id if reference to another driver
     */

    protected String doAttach(ServiceReference deviceSRef, Match match) throws Exception {

        Object driver = Activator.bc.getService(match.getDriver());
        if (driver instanceof Driver) {
            String attachResult = ((Driver) driver).attach(deviceSRef);
            if (attachResult == null) {
                // attach was sucessfull
                Activator.getLogger().info("Driver: " + Utils.driverDetails(match.getDriver()) + " attached to device: " + Utils.deviceDetails(deviceSRef));
            } else {
                // attachement was a referral, not yet treated
                return attachResult;
            }
        }

        return null;
    }

    /**
     * Indicate to device ( if implements Device interface ) that no matching driver was found
     *
     * @param deviceSRef
     */
    protected void doNoDriverFound(ServiceReference deviceSRef) {

        Object device = Activator.bc.getService(deviceSRef);
        if (device instanceof Device) {
            Activator.getLogger().info("no driver found for device: " + Utils.deviceDetails(deviceSRef));
            ((Device) device).noDriverFound();
        }
    }

    /**
     * Get best match from a list of matches
     *
     * @param matches
     * @return Match the best match, or null if provided match list is empty
     */
    protected Match doGetBestMatch(List<Match> matches) {

        if (matches.size() == 0) {
            return null;
        } else if (matches.size() == 1) {
            return matches.get(0);
        } else {
            matches.sort(new MatchesComparator());
            return matches.get(matches.size() - 1);
        }
    }

    private class MatchesComparator implements Comparator<Match> {

        @Override
        public int compare(Match o1, Match o2) {

            int result = 0;
            if (o1.getMatchValue() > o2.getMatchValue()) {
                result = 1;
            } else if (o1.getMatchValue() < o2.getMatchValue()) {
                result = -1;
            } else {
                int o1ServiceRanking = Utils.getServiceRanking(o1.getDriver());
                int o2ServiceRanking = Utils.getServiceRanking(o2.getDriver());

                if (o1ServiceRanking != o2ServiceRanking) {
                    result = o1ServiceRanking - o2ServiceRanking;
                } else {

                    int o1ServiceId = Utils.getServiceId(o1.getDriver());
                    int o2ServiceId = Utils.getServiceId(o2.getDriver());

                    result = o1ServiceId - o2ServiceId;
                }
            }

            return result;
        }
    }
}
