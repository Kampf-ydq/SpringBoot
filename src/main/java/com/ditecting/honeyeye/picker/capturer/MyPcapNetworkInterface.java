package com.ditecting.honeyeye.picker.capturer;

import org.pcap4j.core.*;
import org.pcap4j.util.NifSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/3/27 16:33
 */
@Component
public class MyPcapNetworkInterface {
    private static final Logger logger = LoggerFactory.getLogger(MyPcapNetworkInterface.class);
    private PcapNetworkInterface MY_NIF;

    @Value("${capture.pcapNetworkInterface.enableAutoFind}")
    private boolean enableAutoFind;  //mode of finding PcapNetworkInterface

    public MyPcapNetworkInterface(){}

    @PostConstruct
    private void init(){
        MY_NIF = findMyPcapNetworkInterface();
    }

    /**
     * get MY_NIF
     *
     * @return
     */
    public PcapNetworkInterface getMyPcapNetworkInterface() {
        return MY_NIF;
    }

    /**
     * find PcapNetworkInterface according to EnableAutoFind
     *
     * @return
     */
    private PcapNetworkInterface findMyPcapNetworkInterface() {
        try {
            if(enableAutoFind)
                return findMyPcapNetworkInterfaceAuto();
            else
                return findMyPcapNetworkInterfaceManual();
        } catch (PcapNativeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * find PcapNetworkInterface automatically, return first "correct" nif by default
     *
     * @return
     */
    private PcapNetworkInterface findMyPcapNetworkInterfaceAuto() throws PcapNativeException, IOException {

        List<PcapNetworkInterface> allPnifs = Pcaps.findAllDevs();

        List<PcapNetworkInterface> activePnifs = allPnifs.stream().filter((PcapNetworkInterface pnif) -> {
                                                     return pnif.isUp();
                                                     }).collect(Collectors.toList());

        if(allPnifs == null || allPnifs.size() == 0){
            throw new IOException("No NIF is detected.");
        }

        /**
         * if there is active Pnifs, then search among them. Otherwise search among all detected Pnifs
         */
        if(activePnifs.size() > 0) {
            return searchPnifByIPVAddress(activePnifs);
        }else{
            logger.warn("There is no active NIF, searching among all detected NIFs.");
            return searchPnifByIPVAddress(allPnifs);
        }
    }

    /**
     * find the first Pnif with an IPV4 address
     *
     * @return
     */
    private PcapNetworkInterface searchPnifByIPVAddress(List<PcapNetworkInterface> pnifs) throws IOException {
        boolean existPnif = false;
        String ipv4Address = null;

        if(pnifs == null){
            throw new IOException("No NIF is input.");
        }

        for (PcapNetworkInterface candidatePnif : pnifs) {
            List<PcapAddress> addresses = candidatePnif.getAddresses();
            if (addresses == null) {
                logger.warn("No IP Address in NIF:" + candidatePnif.getName() + ".");
            } else {
                for (PcapAddress pcapAddress : addresses) {
                    if (pcapAddress instanceof PcapIpV4Address) {
                        ipv4Address = pcapAddress.getAddress().getHostAddress();
                        existPnif = true;
                        break;
                    }
                }
                if (existPnif) {
                    if (pnifs.size() > 1) {
                        logger.info("There are " + pnifs.size() + " NIFs, the NIF (" + candidatePnif.getName() + ") is firstly selected with an IPV4 address (" + ipv4Address + ").");
                    } else {
                        logger.info("The NIF (" + candidatePnif.getName() + ") is selected with an IPV4 address (" + ipv4Address + ").");
                    }
                    return candidatePnif;
                }
            }
        }
        throw new IOException("No NIF with an IPV4 address.");
    }

    /**
     * find PcapNetworkInterface by the sequence numbers of NetworkInterfaces
     *
     * @return
     */
    private PcapNetworkInterface findMyPcapNetworkInterfaceManual() throws IOException {
        PcapNetworkInterface nif = new NifSelector().selectNetworkInterface();
        return nif;
    }
}
