package za.co.jumpingbean.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class JavaDayDemo extends ApplicationAdapter {

    private World world;
    private OrthographicCamera camera;
    private ShapeRenderer renderer;
    private Body boxTop;
    private Body boxLeft;
    private Body boxRight;
    private Body ball;
    private Body player;
    private Body ground;

    private final short WALLS = 1;
    private final short GROUND = 2;
    private final short BALL = 4;
    private final short PLAYER = 8;

    @Override
    public void create() {
        world = new World(new Vector2(0, -9.8f), true);
        camera = new OrthographicCamera(20, 20);
        camera.position.set(10f, 10f, 0);
        renderer = new ShapeRenderer();

        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                if ((contact.getFixtureA().getBody() == ball && contact.getFixtureB().getBody() == player)
                        || (contact.getFixtureB().getBody() == ball && contact.getFixtureA().getBody() == player)) {
                        ball.applyLinearImpulse(new Vector2(player.getLinearVelocity().x%3,
                                6),ball.getPosition(), true);
                }
            }

            @Override
            public void endContact(Contact cntct) {
            }

            @Override
            public void preSolve(Contact cntct, Manifold mnfld) {
            }

            @Override
            public void postSolve(Contact cntct, ContactImpulse ci) {
            }

        });

        //Create top box
        BodyDef boxTopDef = new BodyDef();
        boxTopDef.position.set(new Vector2(10, 19));
        boxTopDef.type = BodyType.StaticBody;
        boxTop = world.createBody(boxTopDef);

        FixtureDef boxTopF = new FixtureDef();
        boxTopF.filter.categoryBits = WALLS;
        boxTopF.filter.maskBits = BALL | PLAYER;
        EdgeShape topEdge = new EdgeShape();
        topEdge.set(-10, 0, 10, 0);
        boxTopF.shape = topEdge;
        boxTop.createFixture(boxTopF);

        //Create left box
        BodyDef boxLeftDef = new BodyDef();
        boxLeftDef.position.set(new Vector2(1, 10));
        boxLeftDef.type = BodyType.StaticBody;
        boxLeft = world.createBody(boxLeftDef);

        FixtureDef boxLeftFD = new FixtureDef();
        boxLeftFD.filter.categoryBits = WALLS;
        boxLeftFD.filter.maskBits = BALL | PLAYER;
        EdgeShape leftEdge = new EdgeShape();
        leftEdge.set(0, -10, 0, 10);
        boxLeftFD.shape = leftEdge;
        boxLeft.createFixture(boxLeftFD);

        //Create right box
        BodyDef boxRightDef = new BodyDef();
        boxRightDef.position.set(new Vector2(19, 10));
        boxRightDef.type = BodyType.StaticBody;
        boxRight = world.createBody(boxRightDef);

        FixtureDef boxRightFD = new FixtureDef();
        boxRightFD.filter.categoryBits = WALLS;
        boxRightFD.filter.maskBits = BALL | PLAYER;
        EdgeShape rightEdge = new EdgeShape();
        rightEdge.set(0, -10, 0, 10);
        boxRightFD.shape = rightEdge;
        boxRight.createFixture(boxRightFD);

        //Create ground
        BodyDef groundDef = new BodyDef();
        groundDef.position.set(new Vector2(10, 1f));
        groundDef.type = BodyType.StaticBody;
        ground = world.createBody(groundDef);
        FixtureDef groundF = new FixtureDef();
        groundF.filter.categoryBits = GROUND;
        groundF.filter.maskBits = PLAYER;
        EdgeShape boxBottom = new EdgeShape();
        boxBottom.set(-10, 0, 10, 0);
        groundF.shape = boxBottom;
        ground.createFixture(groundF);

        //Ball
        BodyDef ballDef = new BodyDef();
        ballDef.position.set(new Vector2(10, 17));
        ballDef.type = BodyType.DynamicBody;
        ball = world.createBody(ballDef);

        FixtureDef ballF = new FixtureDef();
        ballF.filter.categoryBits = BALL;
        ballF.filter.maskBits = WALLS | PLAYER;
        CircleShape circle = new CircleShape();
        circle.setPosition(new Vector2(0, 0));
        circle.setRadius(0.5f);
        ballF.shape = circle;
        ballF.restitution = 1f;
        ballF.density = 0.3f;
        ball.createFixture(ballF);

        //Player
        BodyDef playerDef = new BodyDef();
        playerDef.position.set(new Vector2(10, 1.5f));
        playerDef.type = BodyType.DynamicBody;
        playerDef.linearDamping = 0.5f;
        player = world.createBody(playerDef);

        FixtureDef playerF = new FixtureDef();
        playerF.filter.categoryBits = PLAYER;
        playerF.filter.maskBits = WALLS | BALL | GROUND;
        PolygonShape paddle = new PolygonShape();
        paddle.setAsBox(1.5f, 0.5f);
        playerF.shape = paddle;
        playerF.restitution = 0f;
        playerF.density = 1f;
        player.createFixture(playerF);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        world.step(1 / 60f, 6, 3);
        camera.update();
        if (Gdx.input.isKeyPressed(Keys.A)) {
            player.applyLinearImpulse(new Vector2(-2, 0), player.getPosition(), true);
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            player.applyLinearImpulse(new Vector2(2, 0), player.getPosition(), true);
        }
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.BLACK);
        //top edge
        renderer.rect(0, 19, 20, 1);
        //right edge
        renderer.rect(19, 0, 1, 20);
        //left edge
        renderer.rect(0, 0, 1, 20);

        //set ball
        renderer.setColor(Color.BLUE);

        renderer.circle(ball.getPosition().x, ball.getPosition().y,
                ball.getFixtureList().get(0).getShape().getRadius());

        //set player
        renderer.setColor(Color.GREEN);
        renderer.rect(player.getPosition().x - 1.5f, player.getPosition().y - 0.5f, 3, 1);

        renderer.end();

    }
}
