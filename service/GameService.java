package com.group_d.paf_server.service;

import com.group_d.paf_server.entity.Board;
import com.group_d.paf_server.entity.Game;
import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    @Autowired
    private final GameRepository gameRepository;

    @Autowired
    private final PlayerService playerService;

    @Autowired
    private BoardService boardService;

    private Queue<Long> waitingPlayers = new LinkedList<>();

    @Autowired
    public GameService(GameRepository gameRepository, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
    }

    public Game initializeGame(Game game) {
        // Initialisieren des Spielbretts
        Board board = new Board();
        game.setBoard(board);

        // Setzen des aktuellen Zeitstempels
        game.setTimestamp(new Date());

        return gameRepository.save(game);
    }

    public Game endGame(Long gameId, Long winnerId) {
        Game game = findGameById(gameId);
        if (game != null) {
            if (winnerId != 0) {
                game.setWinner(playerService.findPlayerById(winnerId));
                game.setDraw(false);
            } else {
                game.setDraw(true);
            }
            game.setTimestamp(new Date());
            return gameRepository.save(game);
        }
        throw new RuntimeException("Spiel mit ID " + gameId + " nicht gefunden.");
    }

    public Game findGameById(Long id) {
        Optional<Game> game = gameRepository.findById(id);
        if (game.isPresent()) {
            return game.get();
        } else {
            // TODO Exception, wenn das Spiel nicht gefunden wird
            // throw new GameNotFoundException("Spiel mit ID " + id + " nicht gefunden.");
            return null;
        }
    }

    public Game updateGame(Long id, Game gameDetails) {
        Game game = findGameById(id);
        if (game != null) {
            // Aktualisieren der Attribute des Spiels
            game.setPlayer1(gameDetails.getPlayer1());
            game.setPlayer2(gameDetails.getPlayer2());
            game.setWinner(gameDetails.getWinner());
            game.setTimestamp(gameDetails.getTimestamp());
            game.setDraw(gameDetails.isDraw());
            return gameRepository.save(game);
        } else {
            // TODO Exception, wenn das Spiel nicht gefunden wird
            return null;
        }
    }

    public void deleteGame(Long id) {
        Game game = findGameById(id);
        if (game != null) {
            gameRepository.delete(game);
        } else {
            // TODO Exception, wenn das Spiel nicht gefunden wird
        }
    }

    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }

    public synchronized Long findMatch(Long playerId) {
        waitingPlayers.add(playerId);

        if (waitingPlayers.size() >= 2) {
            Long player1Id = waitingPlayers.poll();
            Long player2Id = waitingPlayers.poll();

            // neues Spiel mit den Spielern
            Game game = new Game();
            game.setPlayer1(playerService.findPlayerById(player1Id));
            game.setPlayer2(playerService.findPlayerById(player2Id));

            game = initializeGame(game);
            return game.getId();
        }
        return null; // Kein Match gefunden
    }

    public Game makeMove(Long gameId, int column, Player player) {
        Game game = findGameById(gameId);
        if (game == null) {
            // Spiel nicht gefunden
            throw new RuntimeException("Spiel mit ID " + gameId + " nicht gefunden.");
        }

        Board board = game.getBoard();
        boardService.placeTokenAndSave(board.getId(), column, player);

        // Überprüfe Spielstatus nach dem Zug
        Game.GameState gameState = checkGameState(game);

        if (gameState == Game.GameState.WIN) {
            game.setWinner(player);
        } else if (gameState == Game.GameState.DRAW) {
            game.setDraw(true);
        } else {
            // Das Spiel läuft noch
        }

        return gameRepository.save(game);
    }

    public Player getCurrentPlayer(Long gameId) {
        Game game = findGameById(gameId);
        if (game == null) {
            throw new RuntimeException("Spiel mit ID " + gameId + " nicht gefunden.");
        }

        Board board = game.getBoard();
        if (board == null) {
            throw new RuntimeException("Board nicht gefunden.");
        }

        int player1Count = 0;
        int player2Count = 0;

        //Felder auszählen
        for (String[] row : board.getFields()) {
            for (String field : row) {
                if (Objects.equals(field, game.getPlayer1().getName())) {
                    player1Count++;
                } else if (Objects.equals(field, game.getPlayer2().getName())) {
                    player2Count++;
                }
            }
        }

        //Spieler mit weniger Steinen ist an der Reihe
        Player beginner = player1Count <= player2Count ? game.getPlayer1() : game.getPlayer2();
        Game.GameState gameState = checkGameState(game);
        //Wenn kein stein platziert beginnt Spieler1
        if (gameState == Game.GameState.IN_PROGRESS && (player1Count == 0 && player2Count == 0)){
            beginner = game.getPlayer1();
        }

        // Der Spieler mit weniger Feldern ist an der Reihe
        return beginner;
    }

    public Game.GameState checkGameState(Game game) {
        Player gewinner = playerService.findPlayerByName(hasFourInARow(game));

        // Überprüfen auf Gewinn
        if (gewinner != null) {
            game.setWinner(gewinner);
            return Game.GameState.WIN;
        }

        // Überprüfen auf Unentschieden
        if (game.getBoard().isBoardFull()) {
            game.setDraw(true);
            return Game.GameState.DRAW;
        }

        // Das Spiel läuft noch
        return Game.GameState.IN_PROGRESS;
    }

    private String hasFourInARow(Game game) {
        String[][] fields = game.getBoard().getFields();

        for (int row = 0; row < fields.length; row++) {
            for (int col = 0; col < fields[0].length; col++) {
                String token = fields[row][col];
                if (!token.equals("leer")) {
                    // Horizontal
                    if (col + 3 < fields[0].length &&
                            token.equals(fields[row][col + 1]) &&
                            token.equals(fields[row][col + 2]) &&
                            token.equals(fields[row][col + 3])) {
                        return token; // Gewinner
                    }

                    // Vertikal
                    if (row + 3 < fields.length &&
                            token.equals(fields[row + 1][col]) &&
                            token.equals(fields[row + 2][col]) &&
                            token.equals(fields[row + 3][col])) {
                        return token; // Gewinner
                    }

                    // Diagonal nach unten rechts
                    if (row + 3 < fields.length && col + 3 < fields[0].length &&
                            token.equals(fields[row + 1][col + 1]) &&
                            token.equals(fields[row + 2][col + 2]) &&
                            token.equals(fields[row + 3][col + 3])) {
                        return token; // Gewinner
                    }

                    // Diagonal nach oben rechts
                    if (row - 3 >= 0 && col + 3 < fields[0].length &&
                            token.equals(fields[row - 1][col + 1]) &&
                            token.equals(fields[row - 2][col + 2]) &&
                            token.equals(fields[row - 3][col + 3])) {
                        return token; // Gewinner
                    }
                }
            }
        }
        return null; // Kein Gewinner
    }
}
