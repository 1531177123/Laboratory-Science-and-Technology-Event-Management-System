package com.yiwen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.yiwen.common.Constants;
import com.yiwen.controller.dto.EchartsDTO;
import com.yiwen.controller.dto.PlayRankListDTO;
import com.yiwen.controller.dto.PlayStatisticsDTO;
import com.yiwen.domain.*;
import com.yiwen.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-27
 * @see PlayStatisticsServiceImpl
 **/
@Service
public class PlayStatisticsServiceImpl implements IPlayStatisticsService
{

    @Autowired
    private IDictService dictService;

    @Autowired
    private ILabService labService;

    @Autowired
    private IPlayService playService;

    @Autowired
    private ITeamService teamService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserTeamService userTeamService;

    @Override
    public PlayRankListDTO getLabsRankList(Integer topN)
    {
        PlayRankListDTO resRankList = new PlayRankListDTO();
        List<Dict> dictAwards = dictService.getDictByType(Constants.DICT_TYPE_SCORE);
        Map<String, String> awards2Score = dictAwards.stream().collect(Collectors.toMap(Dict::getName, Dict::getValue));
        Set<String> xAxis = dictAwards.stream().map(Dict::getName).collect(Collectors.toCollection(LinkedHashSet::new));
        xAxis.add("Total/分");
        resRankList.setCrossAxis(xAxis);
        List<Lab> allLabs = labService.list();
        Queue<Map<String, String>> priorityQue = new PriorityQueue<>(Comparator.comparingInt(o -> -Integer.parseInt(o.get("Total/分"))));
        for (Lab lab : allLabs)
        {
            Integer allScore = 0;
            Map<String, String> labAward2Score = new LinkedHashMap<>();
            for (String xAxi : xAxis) {
                labAward2Score.put(xAxi, "0");
            }
            List<Play> plays = playService.getByLabId(lab.getId());
            for (Play play : plays)
            {
                List<Team> teams = teamService.getByPlayId(play.getId());
                for (Team team : teams)
                {
                    String teamScore = team.getScore();
                    if (StrUtil.isNotBlank(teamScore) && awards2Score.containsKey(teamScore))
                    {
                        allScore += Integer.parseInt(awards2Score.get(teamScore));
                        Integer sCount = Integer.parseInt(labAward2Score.get(teamScore));
                        labAward2Score.put(teamScore, String.valueOf(++sCount));
                    }

                }
            }
            labAward2Score.put("Total/分", String.valueOf(allScore));
            labAward2Score.put("labName", lab.getName());
            priorityQue.add(labAward2Score);
        }
       int length = priorityQue.size();
        List<RankListStatistics> verticalAxis = new ArrayList<>();
        for (int i = 0; i < length && i < topN; i++)
        {
            RankListStatistics rankListStatistics = new RankListStatistics();
            Map<String, String> poll = priorityQue.remove();
            rankListStatistics.setName(poll.remove("labName"));
            rankListStatistics.setYAxis(poll);
            verticalAxis.add(rankListStatistics);
        }

        resRankList.setVerticalAxis(verticalAxis);
        return resRankList;
    }

    @Override
    public PlayRankListDTO getPersonRankList(Integer topN)
    {
        PlayRankListDTO resRankList = new PlayRankListDTO();
        List<Dict> dictAwards = dictService.getDictByType(Constants.DICT_TYPE_SCORE);
        Map<String, String> awards2Score = dictAwards.stream().collect(Collectors.toMap(Dict::getName, Dict::getValue));
        Set<String> xAxis = dictAwards.stream().map(Dict::getName).collect(Collectors.toCollection(LinkedHashSet::new));
        xAxis.add("Total/分");
        resRankList.setCrossAxis(xAxis);
        List<UserLogin> allUserLogin = userService.getAllUserLogin();
        Queue<Map<String, String>> priorityQue = new PriorityQueue<>(Comparator.comparingInt(o -> -Integer.parseInt(o.get("Total/分"))));
        for (UserLogin userLogin : allUserLogin)
        {
            String userName = "";
            try
            {
                userName = userService.getCurrentUserDetails(userLogin).getName();
            }
            catch (Exception e)
            {
                continue;
            }
            Integer allScore = 0;
            Map<String, String> labAward2Score = new LinkedHashMap<>();
            for (String xAxi : xAxis) {
                labAward2Score.put(xAxi, "0");
            }
            List<UserTeam> userTeams = userTeamService.getByUserId(userLogin.getId()).stream().filter(userTeam -> Constants.CHECK_PASS.equals(userTeam.getStatus())).collect(Collectors.toList());
            Set<String> teamIds = new LinkedHashSet<>();
            for (UserTeam userTeam : userTeams)
            {
                String teamId = userTeam.getTeamId();
                if (!teamIds.contains(teamId)) {
                    Team team = teamService.getById(userTeam.getTeamId());
                    if (team != null)
                    {
                        String teamScore = team.getScore();
                        if (StrUtil.isNotBlank(teamScore) && awards2Score.containsKey(teamScore))
                        {
                            allScore += Integer.parseInt(awards2Score.get(teamScore)) ;
                            Integer sCount = Integer.parseInt(labAward2Score.get(teamScore));
                            labAward2Score.put(teamScore, String.valueOf(++sCount));
                        }
                    }
                    teamIds.add(teamId);
                }
            }

            labAward2Score.put("Total/分", String.valueOf(allScore));
            labAward2Score.put("userName", userName);
            priorityQue.add(labAward2Score);
        }

        int length = priorityQue.size();
        List<RankListStatistics> verticalAxis = new ArrayList<>();
        for (int i = 0; i < length && i < topN; i++)
        {
            RankListStatistics rankListStatistics = new RankListStatistics();
            Map<String, String> poll = priorityQue.remove();
            rankListStatistics.setName(poll.remove("userName"));
            rankListStatistics.setYAxis(poll);
            verticalAxis.add(rankListStatistics);
        }

        resRankList.setVerticalAxis(verticalAxis);
        return resRankList;
    }

    @Override
    public PlayStatisticsDTO getPlayStatistics()
    {
        PlayStatisticsDTO playStatisticsDTO = new PlayStatisticsDTO();
        playStatisticsDTO.setLabCount(String.valueOf(labService.list().size()));
        playStatisticsDTO.setPlayCount(String.valueOf(playService.list().size()));
        List<Team> teamList = teamService.list().stream().filter(team -> !Constants.STATUS_TEAM_CONVENE.equals(team.getStatus())).collect(Collectors.toList());
        playStatisticsDTO.setTeamCount(String.valueOf(teamList.size()));
        List<Team> awardsTeams = teamService.list().stream().filter(team -> StrUtil.isNotBlank(team.getScore())).collect(Collectors.toList());
        playStatisticsDTO.setAwardsCount(String.valueOf(awardsTeams.size()));
        return playStatisticsDTO;
    }

    @Override
    public EchartsDTO getLabAwardData()
    {

        EchartsDTO echartsDTO = new EchartsDTO();
        List<String> xAxis = new ArrayList<>();
        List<String> yAxis = new ArrayList<>();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        Queue<EchartHandle> echartHandleQueue = getLabAwardDataPre();
        int size = echartHandleQueue.size();
        for (int i = 0; i < size; i++)
        {

            EchartHandle poll = echartHandleQueue.remove();
            xAxis.add(poll.getName());
            yAxis.add(nf.format(poll.getValue() / 100));
        }
        echartsDTO.setXAxis(xAxis);
        echartsDTO.setYAxis(yAxis);
        return echartsDTO;
    }


    private Queue<EchartHandle> getLabAwardDataPre()
    {

        List<Lab> labList = labService.list();
        Queue<EchartHandle> echartHandleQueue = new PriorityQueue<>(new Comparator<EchartHandle>() {
            @Override
            public int compare(EchartHandle o1, EchartHandle o2) {
                return (int) (o1.getValue() - o2.getValue());
            }
        });
        for (Lab lab : labList)
        {
            EchartHandle echartHandle = new EchartHandle();
            echartHandle.setName(lab.getName());
            Double teamSize = 0.0;
            Double awardsTeams = 0.0;
            List<Play> plays = playService.getByLabId(lab.getId());
            for (Play play : plays)
            {
                List<Team> teams = teamService.getByPlayId(play.getId());
                teamSize += teams.size();
                for (Team team : teams) {
                    if (StrUtil.isNotBlank(team.getScore()))
                    {
                        awardsTeams++;
                    }
                }
            }
            echartHandle.setValue((awardsTeams / teamSize) * 100);
            echartHandleQueue.add(echartHandle);
        }
        return echartHandleQueue;
    }

    private Queue<EchartHandle> getLabAwardPieDataPre()
    {

        List<Lab> labList = labService.list();
        Queue<EchartHandle> echartHandleQueue = new PriorityQueue<>(new Comparator<EchartHandle>() {
            @Override
            public int compare(EchartHandle o1, EchartHandle o2) {
                return (int) (o1.getValue() - o2.getValue());
            }
        });
        Double teamSize = 0.0;
        for (Lab lab : labList)
        {
            EchartHandle echartHandle = new EchartHandle();
            echartHandle.setName(lab.getName());
            Double awardsTeams = 0.0;
            List<Play> plays = playService.getByLabId(lab.getId());
            for (Play play : plays)
            {
                List<Team> teams = teamService.getByPlayId(play.getId());
                for (Team team : teams) {
                    if (StrUtil.isNotBlank(team.getScore()))
                    {
                        awardsTeams++;
                    }
                }
            }
            teamSize += awardsTeams;
            echartHandle.setValue((awardsTeams) * 100);
            echartHandleQueue.add(echartHandle);
        }
        return echartHandleQueue;
    }

    @Override
    public List<EchartHandle> getLabAwardPieData()
    {
        List<EchartHandle> resEcharts = new ArrayList<>();
        List<Lab> labList = labService.list();
        Queue<EchartHandle> echartHandleQueue = new PriorityQueue<>(new Comparator<EchartHandle>() {
            @Override
            public int compare(EchartHandle o1, EchartHandle o2) {
                return (int) (o1.getValue() - o2.getValue());
            }
        });
        for (Lab lab : labList)
        {
            EchartHandle echartHandle = new EchartHandle();
            echartHandle.setName(lab.getName());
            Double awardsTeams = 0.0;
            List<Play> plays = playService.getByLabId(lab.getId());
            for (Play play : plays)
            {
                List<Team> teams = teamService.getByPlayId(play.getId());
                for (Team team : teams) {
                    if (StrUtil.isNotBlank(team.getScore()))
                    {
                        awardsTeams++;
                    }
                }
            }
            echartHandle.setValue(awardsTeams);
            echartHandleQueue.add(echartHandle);
        }
        int size = echartHandleQueue.size();
        for (int i = 0; i < size; i++)
        {
            EchartHandle remove = echartHandleQueue.remove();
            try {
                remove.setValue(remove.getValue());
            }catch (Exception e)
            {
                remove.setValue(0.00);
            }
            resEcharts.add(remove);
        }
        return resEcharts;
    }
}
