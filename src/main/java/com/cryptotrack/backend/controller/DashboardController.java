package com.cryptotrack.backend.controller;

import com.cryptotrack.backend.entity.User;
import com.cryptotrack.backend.entity.Watchlist;
import com.cryptotrack.backend.service.UserService;
import com.cryptotrack.backend.service.WatchlistService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final WatchlistService watchlistService;
    private final UserService userService;

    public DashboardController(
            WatchlistService watchlistService,
            UserService userService) {

        this.watchlistService = watchlistService;
        this.userService = userService;
    }

    @GetMapping("/watchlist")
    public List<Watchlist> getWatchlist(
            @RequestParam String email) {

        User user = userService.findByEmail(email);

        return watchlistService.getUserWatchlist(user);
    }
}