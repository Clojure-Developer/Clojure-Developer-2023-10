(ns otus-21.homework.core
    (:require [clojure.string :as str]
              [clojure.walk :as w]
              [clojure.zip :as z]))
(def inp2 "$ cd /\n$ ls\ndir lhrfs\n193233 mvsjmrtn\ndir nwh\ndir pjsd\ndir qfrrtb\n31987 zzdfcs\n$ cd lhrfs\n$ ls\n197903 hzl.jdj\n42249 wsbpzmbq.hws\n$ cd ..\n$ cd nwh\n$ ls\n63077 bgrccm.tqh\n69961 dznccwl.bnw\ndir pmdj\n187013 rsbvj.jtd\n$ cd pmdj\n$ ls\n292527 rlgfd.rrd\n68737 tbj.grn\n153072 wsbpzmbq.hws\n$ cd ..\n$ cd ..\n$ cd pjsd\n$ ls\ndir czzcslm\ndir dgwpl\ndir fqg\ndir lszhdjr\ndir mmpf\ndir wtwhzzwz\n149748 zzdfcs\n$ cd czzcslm\n$ ls\n249237 bvrnzhd.vzp\n16960 ssvqllt.ccv\n$ cd ..\n$ cd dgwpl\n$ ls\n23547 brsbfqbm.hls\ndir ljzrwpv\n$ cd ljzrwpv\n$ ls\ndir btnzjtlr\ndir czr\n$ cd btnzjtlr\n$ ls\n191998 tbj.mwg\n$ cd ..\n$ cd czr\n$ ls\ndir fqg\n$ cd fqg\n$ ls\n276766 llhzr.pjh\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd fqg\n$ ls\n275942 bhcg\ndir fqg\ndir gghdbzz\ndir hswgvpt\n75273 hzl\ndir lfpblmwt\ndir sdcwn\ndir wmrwd\n$ cd fqg\n$ ls\ndir bpbtnq\n267620 btnzjtlr.mgr\n174395 fqg.vpw\ndir tfbfgzw\ndir twhvbbr\n$ cd bpbtnq\n$ ls\n194542 mtztnd.hdb\n$ cd ..\n$ cd tfbfgzw\n$ ls\ndir cgdlflbt\n10814 fqg.cqp\n40235 fztb.jzr\ndir hzl\n$ cd cgdlflbt\n$ ls\n176005 wsbpzmbq.hws\n$ cd ..\n$ cd hzl\n$ ls\ndir ccpdp\ndir hgwpjvn\ndir hzl\n203730 pbn.rzl\n88179 tbj\n295466 zdbmfmzs.jjh\n$ cd ccpdp\n$ ls\n172836 btnzjtlr\n$ cd ..\n$ cd hgwpjvn\n$ ls\n122725 hnsrdnl.ctv\n261470 tmgpjbj\n$ cd ..\n$ cd hzl\n$ ls\n59843 lffmd.fwr\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd twhvbbr\n$ ls\n134874 hnsrdnl.ctv\n$ cd ..\n$ cd ..\n$ cd gghdbzz\n$ ls\ndir fqg\n$ cd fqg\n$ ls\n233336 ssvqllt.ccv\n$ cd ..\n$ cd ..\n$ cd hswgvpt\n$ ls\ndir fwfhnbc\ndir hcp\n221595 hnsrdnl.ctv\n230875 hzl\n257695 jbfnlc.qqn\n126050 tbj.qlc\ndir wdlh\n$ cd fwfhnbc\n$ ls\n293848 zhz.mff\n$ cd ..\n$ cd hcp\n$ ls\n288118 gdndr.gwn\n$ cd ..\n$ cd wdlh\n$ ls\n227328 btnzjtlr.msq\ndir hzl\n94325 hzl.wjp\n272466 zzdfcs\n$ cd hzl\n$ ls\n184779 ssvqllt.ccv\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd lfpblmwt\n$ ls\n151071 ssvqllt.ccv\n$ cd ..\n$ cd sdcwn\n$ ls\n61632 hzl.mvp\ndir tbj\ndir tdwc\ndir tnjcf\ndir tstqdt\n$ cd tbj\n$ ls\ndir btnzjtlr\ndir cwsv\n1318 tbj\n293645 wnv\n$ cd btnzjtlr\n$ ls\n2441 hnsrdnl.ctv\n$ cd ..\n$ cd cwsv\n$ ls\n238930 gwr\n236116 tlzqtch\n$ cd ..\n$ cd ..\n$ cd tdwc\n$ ls\ndir btnzjtlr\ndir fqg\ndir zgjpfj\n$ cd btnzjtlr\n$ ls\n177427 tbj.hgf\n$ cd ..\n$ cd fqg\n$ ls\n241003 dbnwzbn.flv\n$ cd ..\n$ cd zgjpfj\n$ ls\n249565 fqg\n238012 tbj.csq\n$ cd ..\n$ cd ..\n$ cd tnjcf\n$ ls\n188504 lljwfglp.wnb\n35384 wsbpzmbq.hws\n$ cd ..\n$ cd tstqdt\n$ ls\ndir lszhdjr\n$ cd lszhdjr\n$ ls\n252181 ssvqllt.ccv\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd wmrwd\n$ ls\n117914 btnzjtlr\n224916 hnsrdnl.ctv\ndir lszhdjr\n112511 trjm.mrw\n20365 trn\n$ cd lszhdjr\n$ ls\n183834 sdnlhh.ntt\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd lszhdjr\n$ ls\ndir gjlms\ndir hrwjmrwf\ndir vblznslv\n$ cd gjlms\n$ ls\n301712 bbs.fhq\ndir btnzjtlr\n148329 cnvlrpbs.tqv\ndir hzl\n107466 hzl.zps\ndir jgflpb\n9865 trjm.mrw\n$ cd btnzjtlr\n$ ls\n60204 nhmc\n$ cd ..\n$ cd hzl\n$ ls\n99518 lszhdjr.stz\n$ cd ..\n$ cd jgflpb\n$ ls\n251545 hzl.ncm\n$ cd ..\n$ cd ..\n$ cd hrwjmrwf\n$ ls\n65264 btnzjtlr.qgg\n123207 pqsn\n30133 zzdfcs\n$ cd ..\n$ cd vblznslv\n$ ls\n19460 hzl\n$ cd ..\n$ cd ..\n$ cd mmpf\n$ ls\n200340 dnsq\n109756 snl\n$ cd ..\n$ cd wtwhzzwz\n$ ls\ndir bpwjhpgr\ndir btnzjtlr\ndir dgqljsbq\ndir fqg\ndir mbbtzgmf\ndir vvmzhhtv\ndir wglhbp\ndir zcwmf\n$ cd bpwjhpgr\n$ ls\n1514 mjczjz\n$ cd ..\n$ cd btnzjtlr\n$ ls\n209165 lszhdjr\n$ cd ..\n$ cd dgqljsbq\n$ ls\n160750 btnzjtlr\ndir fqg\ndir hrncl\n5964 hzvr.ftp\ndir jldnlddj\n267082 qdc.grf\n205213 sjpdjt.ngt\ndir vws\n175214 wzwwqq.stp\n$ cd fqg\n$ ls\ndir ghsdjhj\n$ cd ghsdjhj\n$ ls\n166720 jqcjngn.fmf\n298171 zzdfcs\n$ cd ..\n$ cd ..\n$ cd hrncl\n$ ls\ndir hzl\n82355 trjm.mrw\ndir vrqbf\n261260 wsbpzmbq.hws\n18593 wtmlmprg.whh\n$ cd hzl\n$ ls\n344 drndgmgz.flz\n241877 zzdfcs\n$ cd ..\n$ cd vrqbf\n$ ls\ndir blf\ndir pzrfw\ndir tbj\n$ cd blf\n$ ls\n68629 wdhfbtj.ncc\n$ cd ..\n$ cd pzrfw\n$ ls\ndir hzl\ndir mdh\ndir tbj\n$ cd hzl\n$ ls\n12677 gvdh.ltp\n$ cd ..\n$ cd mdh\n$ ls\n222003 qbvfdv\n$ cd ..\n$ cd tbj\n$ ls\ndir wtmcqwgp\n$ cd wtmcqwgp\n$ ls\ndir stcgrs\n$ cd stcgrs\n$ ls\n285843 tbj\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd tbj\n$ ls\n173380 mcgzmthd.mdg\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd jldnlddj\n$ ls\n57836 brdtwc.pws\ndir fqg\ndir hzl\n140902 lbn\ndir qhrcm\ndir ssrc\n169907 wsbpzmbq.hws\n$ cd fqg\n$ ls\n273621 pwjr.gwt\n$ cd ..\n$ cd hzl\n$ ls\ndir fqg\ndir tbj\n$ cd fqg\n$ ls\n290882 hcf.sqw\n62759 hlntl.zqg\n115593 ntgm.wjn\n64481 qwtv\n$ cd ..\n$ cd tbj\n$ ls\n199421 gsvpcdm\n200467 hnsrdnl.ctv\n9980 lgqgdvwz.zdp\ndir lqlml\n19593 pgn\n51598 tqqbwd\n$ cd lqlml\n$ ls\n92416 fdwgg.rdp\n279285 zzdfcs\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd qhrcm\n$ ls\n278543 trjm.mrw\n279502 zzdfcs\n$ cd ..\n$ cd ssrc\n$ ls\n238789 zfvwwnhl\n$ cd ..\n$ cd ..\n$ cd vws\n$ ls\n249170 bgfrh\ndir ndtnt\n$ cd ndtnt\n$ ls\n5224 qwdnzdq.rfz\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd fqg\n$ ls\n171994 trjm.mrw\n$ cd ..\n$ cd mbbtzgmf\n$ ls\ndir cfqzlgm\n57211 ssvqllt.ccv\ndir tbj\n$ cd cfqzlgm\n$ ls\ndir tlbgq\n$ cd tlbgq\n$ ls\n232962 ssvqllt.ccv\n$ cd ..\n$ cd ..\n$ cd tbj\n$ ls\n23083 lszhdjr.lzt\n$ cd ..\n$ cd ..\n$ cd vvmzhhtv\n$ ls\n150567 fcpv.vws\n$ cd ..\n$ cd wglhbp\n$ ls\ndir cjm\ndir gjhzw\ndir lmzzlp\n123986 lszhdjr.hcj\n275863 lwbz\n121332 snnhqgp.tdj\ndir stngv\ndir wdzvcfm\ndir wjqztbj\ndir wzhsq\n$ cd cjm\n$ ls\n300643 jhtjsn.hzm\n$ cd ..\n$ cd gjhzw\n$ ls\ndir hwlj\ndir tbj\n$ cd hwlj\n$ ls\n280279 ssvqllt.ccv\n$ cd ..\n$ cd tbj\n$ ls\n88746 ssvqllt.ccv\n139206 tbj\n$ cd ..\n$ cd ..\n$ cd lmzzlp\n$ ls\n9261 btnzjtlr\ndir fqg\n128118 fqg.fsf\n165323 hnsrdnl.ctv\ndir prvmm\n$ cd fqg\n$ ls\n35303 dcglqd.zrj\n71812 mvzh\n32361 ssvqllt.ccv\n69305 zzdfcs\n$ cd ..\n$ cd prvmm\n$ ls\n296946 zzdfcs\n$ cd ..\n$ cd ..\n$ cd stngv\n$ ls\ndir hgj\n46259 vlb.ztz\n25946 zstwl.wgs\n106712 zzdfcs\n$ cd hgj\n$ ls\n289819 dhwr.mvc\n85418 dvpvhhgj.fmw\n108543 fqg.frj\n229544 pwhwmctv\n$ cd ..\n$ cd ..\n$ cd wdzvcfm\n$ ls\ndir sdphr\n$ cd sdphr\n$ ls\n84638 hnsrdnl.ctv\n$ cd ..\n$ cd ..\n$ cd wjqztbj\n$ ls\n240174 btnzjtlr.ldw\n125567 lszhdjr\n265718 lszhdjr.mmz\n242407 wzjcc\n$ cd ..\n$ cd wzhsq\n$ ls\ndir hzl\n22176 hzl.cpv\n122990 ldtwhvc.vcv\n$ cd hzl\n$ ls\n309519 plnn.pdn\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd zcwmf\n$ ls\ndir hwq\n268508 mztd\n$ cd hwq\n$ ls\n189618 trjm.mrw\n120145 wsbpzmbq.hws\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd qfrrtb\n$ ls\ndir dmntr\ndir fqg\ndir gpcvsbpl\ndir hzl\ndir mqbhs\ndir pgvngj\ndir pnrdwlqn\ndir qlnwhq\n$ cd dmntr\n$ ls\ndir btnzjtlr\n141598 fqg.wcw\n223036 hnsrdnl.ctv\n96925 qhwmj\n257697 ssvqllt.ccv\n184839 vmbfhldv.zgm\n$ cd btnzjtlr\n$ ls\n153689 jnbthqwp\ndir tcp\n$ cd tcp\n$ ls\n90378 mczzfwsz.hwf\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd fqg\n$ ls\ndir btq\n51590 fqg\n176455 gnqsnpj.vsh\ndir mthwtst\ndir trhz\ndir zbpjvb\n$ cd btq\n$ ls\ndir bdnc\ndir ttpj\ndir vftshfd\n$ cd bdnc\n$ ls\n263358 trjm.mrw\n$ cd ..\n$ cd ttpj\n$ ls\n185501 fqg.dvq\n151107 ltmmsr.lqd\ndir mbtqmqh\n231236 sdzp.qhb\n16601 vfflgw.vrr\n75487 zbvllh.gqb\n$ cd mbtqmqh\n$ ls\ndir lszhdjr\n184660 pmwswf.lrm\ndir tbj\ndir zqbss\n262042 zzdfcs\n$ cd lszhdjr\n$ ls\ndir dmc\n$ cd dmc\n$ ls\n173189 mzmr.nrj\n$ cd ..\n$ cd ..\n$ cd tbj\n$ ls\n59531 fjqfbq\n$ cd ..\n$ cd zqbss\n$ ls\ndir btnzjtlr\ndir lszhdjr\n14365 sqdj\n$ cd btnzjtlr\n$ ls\n113619 trjm.mrw\n$ cd ..\n$ cd lszhdjr\n$ ls\n87142 hnsrdnl.ctv\ndir lszhdjr\n$ cd lszhdjr\n$ ls\ndir jsdrvbhc\n$ cd jsdrvbhc\n$ ls\n210295 hzl\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd vftshfd\n$ ls\ndir jjws\ndir mvdcjgp\ndir qflcrlrm\n$ cd jjws\n$ ls\n82848 trjm.mrw\n$ cd ..\n$ cd mvdcjgp\n$ ls\n231865 lzzl\n$ cd ..\n$ cd qflcrlrm\n$ ls\n150667 btnzjtlr\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd mthwtst\n$ ls\n26308 lszhdjr.dbb\n$ cd ..\n$ cd trhz\n$ ls\n16138 swtdz.hdt\n$ cd ..\n$ cd zbpjvb\n$ ls\n69598 hnsrdnl.ctv\n96314 tbj.gpj\n$ cd ..\n$ cd ..\n$ cd gpcvsbpl\n$ ls\ndir dbssfzqt\ndir pgn\ndir zfgvldv\n$ cd dbssfzqt\n$ ls\n156599 jcv\n68192 zzdfcs\n$ cd ..\n$ cd pgn\n$ ls\n124613 fqg\n139219 gmnqvlbb.nnf\n177527 hnsrdnl.ctv\n116238 hzl.qwm\ndir tbj\n69576 zzdfcs\n$ cd tbj\n$ ls\n78863 hnsrdnl.ctv\ndir lszhdjr\n$ cd lszhdjr\n$ ls\n76043 qjwjw.dbn\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd zfgvldv\n$ ls\ndir tbj\n$ cd tbj\n$ ls\n308796 gqcw.fsm\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd hzl\n$ ls\n50784 btnzjtlr\n19755 gjbcgphh.rbw\n169847 lncnj.bct\n46359 mbrtgl\ndir nlnt\n269695 wfg.cdn\n$ cd nlnt\n$ ls\ndir cqfzqwr\ndir dghdql\ndir fnd\n159573 hnsrdnl.ctv\ndir lszhdjr\n16814 tsm\n59202 wsbpzmbq.hws\n22244 zlbj\n$ cd cqfzqwr\n$ ls\n213009 trjm.mrw\n$ cd ..\n$ cd dghdql\n$ ls\n61202 hnsrdnl.ctv\n$ cd ..\n$ cd fnd\n$ ls\n237330 hnsrdnl.ctv\n$ cd ..\n$ cd lszhdjr\n$ ls\n222813 ssvqllt.ccv\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd mqbhs\n$ ls\ndir fqg\n$ cd fqg\n$ ls\n121590 lcwsbvw.jlj\n13769 zzdfcs\n$ cd ..\n$ cd ..\n$ cd pgvngj\n$ ls\n29079 lnfsw.mvd\ndir tbj\n$ cd tbj\n$ ls\n303514 ssvqllt.ccv\n81789 wsbpzmbq.hws\n$ cd ..\n$ cd ..\n$ cd pnrdwlqn\n$ ls\n145498 btnzjtlr.hdq\n209811 fqg\ndir gscsq\n39287 jbln.grc\ndir lszhdjr\ndir zcj\n$ cd gscsq\n$ ls\ndir btnzjtlr\n257666 cmflqncq.csp\n146453 lszhdjr.jvl\n21252 ssvqllt.ccv\ndir vjpgs\n29627 zjgswm.zmw\n$ cd btnzjtlr\n$ ls\n52870 fqg.bcq\n131279 hnsrdnl.ctv\ndir hts\n180154 plgqz.lfz\n118677 ssvqllt.ccv\n143055 zzdfcs\n$ cd hts\n$ ls\n295829 hnsrdnl.ctv\n$ cd ..\n$ cd ..\n$ cd vjpgs\n$ ls\n247444 fqg.rjw\ndir hzl\n113248 tbj\n$ cd hzl\n$ ls\n91966 ppff.qhn\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd lszhdjr\n$ ls\n290399 grzs.btl\n11125 gswg\n110268 mhdfszz.pdh\ndir mwbjshb\ndir ndtllttm\n126485 wsbpzmbq.hws\n152691 zzdfcs\n$ cd mwbjshb\n$ ls\n119607 rpgml\n$ cd ..\n$ cd ndtllttm\n$ ls\n5107 qsqqfpc.mzf\n$ cd ..\n$ cd ..\n$ cd zcj\n$ ls\ndir gftzs\n131397 ssvqllt.ccv\ndir vpqvpmv\ndir wdqw\n34540 wsbpzmbq.hws\n$ cd gftzs\n$ ls\n115531 fbjj.vrn\n$ cd ..\n$ cd vpqvpmv\n$ ls\ndir bfqpwgdc\n67619 ffbllv\ndir tzr\n$ cd bfqpwgdc\n$ ls\n144904 fqg.djq\ndir gbn\ndir jzz\ndir vtp\n$ cd gbn\n$ ls\ndir btnzjtlr\n$ cd btnzjtlr\n$ ls\ndir dzsbcqjd\n253792 fqg.fcw\n209245 fqtqsdrt.pqd\n303280 nqvqm\n271246 vchvvq.cft\n$ cd dzsbcqjd\n$ ls\n13953 tbj\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd jzz\n$ ls\n234810 ssvqllt.ccv\n$ cd ..\n$ cd vtp\n$ ls\n287225 rzqfq.pvj\n$ cd ..\n$ cd ..\n$ cd tzr\n$ ls\n135596 bbzlnjtc.fft\ndir bqfz\ndir dztgr\n306047 hnsrdnl.ctv\n248180 jzdzf\ndir lszhdjr\ndir mds\ndir nmdc\ndir qpmt\n126445 wsbpzmbq.hws\n191907 zzdfcs\n$ cd bqfz\n$ ls\n137988 fqg\n122217 lszhdjr.bwc\n232293 ssvqllt.ccv\n168937 twrtmwh.ddc\n$ cd ..\n$ cd dztgr\n$ ls\n23653 qhrp.ljh\n$ cd ..\n$ cd lszhdjr\n$ ls\ndir ghqlj\ndir qjjvfv\ndir qsjrnq\n$ cd ghqlj\n$ ls\n184316 ghshmzt.srl\n258282 nbqndwj\ndir ndd\ndir tqqlnw\n68843 trjm.mrw\n92593 wllcqzfr.mbd\n$ cd ndd\n$ ls\n152924 gvclc\n77856 llztchwp.jjd\n$ cd ..\n$ cd tqqlnw\n$ ls\n4090 flvdsc.zsv\n219378 vgwfn.zjh\n$ cd ..\n$ cd ..\n$ cd qjjvfv\n$ ls\n230606 trjm.mrw\n$ cd ..\n$ cd qsjrnq\n$ ls\n252853 fnnbmt.dtm\n279660 lszhdjr.rjc\n$ cd ..\n$ cd ..\n$ cd mds\n$ ls\n10903 jzwjv\n$ cd ..\n$ cd nmdc\n$ ls\n114234 hnsrdnl.ctv\n$ cd ..\n$ cd qpmt\n$ ls\n171542 bvrzgp\n129238 hnsrdnl.ctv\n137570 tbj\n54929 vtcfq.npn\n44117 zzdfcs\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd wdqw\n$ ls\ndir jmvsdlv\ndir mjgpdcbl\n$ cd jmvsdlv\n$ ls\ndir flnnqz\n$ cd flnnqz\n$ ls\n224693 flqpwqp.fwn\n$ cd ..\n$ cd ..\n$ cd mjgpdcbl\n$ ls\n140928 btnzjtlr.prd\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd qlnwhq\n$ ls\ndir btnzjtlr\ndir cbhr\ndir cnbssw\ndir dwvv\ndir lszhdjr\ndir rnhnbs\ndir sdhqp\n$ cd btnzjtlr\n$ ls\n225671 ssrcp.chb\n268250 tbj.cfd\n$ cd ..\n$ cd cbhr\n$ ls\n68468 qdqlml.qrj\n$ cd ..\n$ cd cnbssw\n$ ls\ndir tbj\n187921 zscs\n$ cd tbj\n$ ls\n307230 hzl\ndir jbcnnvq\n228268 lssvr.gfn\n$ cd jbcnnvq\n$ ls\n277047 nztsr\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd dwvv\n$ ls\n267743 btnzjtlr\ndir fqg\n68364 qznpsjp\ndir wdr\n230595 wsbpzmbq.hws\n206 wtwwd.jnb\n$ cd fqg\n$ ls\n260003 btnzjtlr.hjn\n103775 btnzjtlr.ndh\n200945 trjm.mrw\ndir twpplmhh\n$ cd twpplmhh\n$ ls\ndir tvrq\n$ cd tvrq\n$ ls\n298383 ssvqllt.ccv\n$ cd ..\n$ cd ..\n$ cd ..\n$ cd wdr\n$ ls\n248357 tbj\n$ cd ..\n$ cd ..\n$ cd lszhdjr\n$ ls\n259605 lszhdjr.mvw\n$ cd ..\n$ cd rnhnbs\n$ ls\n297597 hnsrdnl.ctv\n$ cd ..\n$ cd sdhqp\n$ ls\n95362 wsbpzmbq.hws")

(def test-map
    {:a     {:e     {:i 584}
             :f     29116
             :g     2557
             :h.lst 62596}
     :b.txt 14848514
     :c.dat 8504156
     :d     {:j     4060174
             :d.log 8033020
             :d.ext 5626152
             :k     7214296}})

(defn map-zipper [m]
    (z/zipper
        (fn [x] (or (map? x) (map? (nth x 1))))
        (fn [x] (seq (if (map? x) x (nth x 1))))
        (fn [x children]
            (if (map? x)
                (into {} children)
                (assoc x 1 (into {} children))))
        m))

(defn spaces-split [s]
    (str/split s #" "))

(def extract-arg-as-kw
    (comp keyword last spaces-split first))

(def extract-file-size
    (comp parse-long first spaces-split first))

;Maybe some is better?
(defn find-in-lazy [lazy key]
    (first (filter #(= (first (first %)) key) lazy)))

(some #(when (even? %) %) [1 2 3 4])

(defn find-direction [zipper re-seq]
    (let [folder-kw (extract-arg-as-kw re-seq)
          curr-layer (iterate z/right (z/down zipper))
          direction (find-in-lazy curr-layer folder-kw)]
        direction))

(defn add-dir [zipper re-seq]
    (let [folder-kw (extract-arg-as-kw re-seq)]
        (z/append-child zipper {folder-kw {}})))

(defn add-file [zipper re-seq]
    (let [file-kw (extract-arg-as-kw re-seq)
          size (extract-file-size re-seq)]
        (z/append-child zipper {file-kw size})))

(defn command-dispatch [zipper command]
    (condp (comp seq re-seq) command
        #"\$ cd /" :>> (constantly (map-zipper (z/root zipper)))
        #"\$ cd \.\." :>> (constantly (z/up zipper))
        #"\$ cd .+" :>> (partial find-direction zipper)
        #"\$ ls" :>> (constantly zipper)
        #"dir .+" :>> (partial add-dir zipper)
        #"\d+ .+" :>> (partial add-file zipper)
        ))

(defn construct-tree [input]
    (z/root (reduce command-dispatch (map-zipper {}) (str/split-lines input))))
(defn evaluate [acc node]
    (if (map? node)
        (let [sum (reduce + (vals node))]
            (if (< sum 100000)
                (swap! acc + sum))
            sum)
        node))

(defn sum-of-sizes [input]
    "По журналу сеанса работы в терминале воссоздаёт файловую систему
  и подсчитывает сумму размеров директорий, занимающих на диске до
  100000 байт (сумма размеров не учитывает случай, когда найденные
  директории вложены друг в друга: размеры директорий всё так же
  суммируются)."
    (let [tree (construct-tree input)
          acc (atom 0)]
        (w/postwalk (partial evaluate acc) tree)
        @acc))