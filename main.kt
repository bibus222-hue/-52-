import de.fabmax.kool.KoolApplication
import de.fabmax.kool.addScene //функци добавить сцену

import de.fabmax.kool.math.Vec3f //3D - vector
import de.fabmax.kool.math.deg //deg - превращение числа в градусы
import de.fabmax.kool.scene.*

import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.util.Color //Цветовая палитра
import de.fabmax.kool.util.Time // Время deltaT - сколько прошло секунд между двумя кадрам

import de.fabmax.kool.pipeline.ClearColorLoad // Режим говорящий нашей команде не очищать экран от элементов нужен для UI

import de.fabmax.kool.modules.ui2.* // импорт всех компонентов интерфейса, вроде text button
import de.fabmax.kool.modules.ui2.UiModifier

class GameState {
    val playerId = mutableStateOf("Player")
    //создает состояние за которым умеет наблюдать и меняться UI
    // Если состояние игрока (его хп) изменилось -> перерисует интерфес для игрока

    val hp = mutableStateOf(100)
    val gold = mutableStateOf(0)
    val potionTicksLeft = mutableStateOf(0)
    //Тики - условные единицы измерения времени в игровом мире
    //У нас на примере будет 1 тик = 1 секунда
}
fun main() = KoolApplication{
    //KoolApplication - запуск движка
    val game = GameState()

    addScene {
        //Добавление сцены игровой
        defaultOrbitCamera()
        //Готовая камера легко перемещается мышью по умолчанию

        //Добавление обьекта на сцену
        addColorMesh { //Добавить цветной текстурированный обьект
            generate { //генерация вершин фигуры
                cube{   //пресет генерации - куб
                    colored() //автоматом создаст разные цвета разным граням фигуры
                }
            }
            shader = KslPbrShader{
                color {vertexColor()}
                metallic(0f) //Металлизация объекта
                roughness(0.25f) //Шероховатость (0f - глянцевый / 1f - матовый)
            }

            onUpdate{
                //метод который выполняется каждый кадр игры
                transform.rotate(45f.deg * Time.deltaT, Vec3f.X_AXIS)
                //rotate(угол, ось)
                //*Time.deltaT - простая формула подсчета того, сколько прошло секунд
            }
        }
        lighting.singleDirectionalLight {
            setup(Vec3f(-1f, -1f, -1f))
            setColor(Color.WHITE,5f)
        }
        var potionTimeSec = 0f

        onUpdate{
            if(game.potionTicksLeft.value > 0) {
                potionTimeSec += Time.deltaT

                if (potionTimeSec >= 1f) {
                    potionTimeSec = 0f
                    game.potionTicksLeft.value = game.potionTicksLeft.value - 1

                    game.hp.value = (game.hp.value - 2).coerceAtLeast(0)
                }
            }else{
                potionTimeSec = 0f
            }
        }
    }
    addScene {
        setupUiScene(ClearColorLoad)

        addPanelSurface {
            modifier
                .size(300.dp, 210.dp)
                .align(AlignmentX.Start, AlignmentY.Top)
                .padding(16.dp)
                .background(RoundRectBackground(Color(0f,0f,0f,0.5f), 14.dp))

            Column {
                //use() - Прочитать состояние - подписаться на него и реагирвоать на его изменения
                Text("Игрок: ${game.playerId.use()}"){}
                Text("HP: ${game.hp.use()}"){}
                Text("Gold: ${game.gold.use()}"){}
                Text("Действие зелья: ${game.potionTicksLeft.use()}"){}
            }

            Row{
                modifier.padding(12.dp)

                Button("Урон hp - 10"){
                    modifier
                        .padding(end = 8.dp)
                        //Отступ не со всех сторон а только справа
                        .onClick{
                            game.hp.value = (game.hp.value - 10).coerceAtLeast(0)
                        }
                }
                Button("Gold + 5"){
                    modifier
                        .padding(end = 8.dp)
                        //Отступ не со всех сторон а только справа
                        .onClick{
                            game.gold.value = (game.gold.value + 5)
                        }

                }

                Button("Наложить эффект"){
                    modifier
                        .padding(end = 8.dp)
                        //Отступ не со всех сторон а только справа
                        .onClick{
                            game.potionTicksLeft.value = (game.potionTicksLeft.value + 5)
                        }

                }
            }
        }
    }
}
